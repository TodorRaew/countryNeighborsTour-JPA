package com.example.countryNeighborsTour.service;

import com.example.countryNeighborsTour.dto.TravelRequest;
import com.example.countryNeighborsTour.exeptions.InvalidInputException;
import com.example.countryNeighborsTour.model.Country;
import com.example.countryNeighborsTour.model.Neighbor;
import com.example.countryNeighborsTour.model.User;
import com.example.countryNeighborsTour.repository.CountryRepository;
import com.example.countryNeighborsTour.repository.NeighborRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final NeighborRepository neighborRepository;
    private final UserService userService;

    @Override
    public String saveCountry(String name, String abrev, String username) {

        Pattern pattern = Pattern.compile("^[a-zA-Z]+(\\s[a-zA-Z]+)*$");
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches() || name.equalsIgnoreCase("null")) {
            throw new InvalidInputException("Incorrect country name");
        }

        pattern = Pattern.compile("^[A-Z]{3}$");
        matcher = pattern.matcher(abrev);

        if (!matcher.matches()) {
            throw new InvalidInputException("The currency abrev cannot be null, empty or with length different from 3");
        }

        if (userService.findByUserName(username).isPresent()) {
            if (userService.findByUserName(username).get().getIsOnline() == 1) {
                if (!countryRepository.findByCountryName(name).isPresent()) {
                    Country country = new Country(name, abrev);
                    countryRepository.save(country);

                    return name + " has registered completely";
                }
                return "Country name already exists!";
            }
            throw new InvalidInputException("User is offline");
        }
        throw new InvalidInputException("Wrong username or password");
    }

    @Override
    public String removeCountry(String name, String username) {

        if (userService.findByUserName(username).isPresent()) {
            if (userService.findByUserName(username).get().getIsOnline() == 1) {
                Optional<Country> country = countryRepository.findByCountryName(name);

                if (country.isPresent()) {
                    countryRepository.deleteById(country.get().getCountryId());
                    return "Country was deleted successfully";
                }
                return "Country not found!";
            }
            throw new InvalidInputException("User is offline");
        }
        throw new InvalidInputException("Wrong username or password");
    }

    @Override
    public Map<String, Double> getRates() throws JsonProcessingException {

        String API_ACCESS_KEY = "Pnu2lD4cT04LQ3QYKlm0WafDfWQK465B";

        String symbols =
                "BGN, RON, MKD, RSD, " +
                        "TRY, GEL, AMD, IRR, IQD, AZN, " +
                        "SYP, ALL, BAM, HUF, UAH, MDL";
        String base = "EUR";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.set("apikey", API_ACCESS_KEY);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("https://api.apilayer.com/exchangerates_data/latest")
                .queryParam("symbols", symbols)
                .queryParam("base", base);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(response.getBody(), Map.class);
        Map<String, Double> rates = (Map<String, Double>) jsonMap.get("rates");

        if (!rates.isEmpty()) {
            return rates;
        } else {
            return new TreeMap<>();
        }
    }

    @Override
    public StringBuilder execute(String username, TravelRequest travelRequest) throws JsonProcessingException {

        Optional<User> user = userService.findByUserName(username);
        if (!user.isPresent()) {
            throw new InvalidInputException("User with username " + username + "does not exist");
        }

        if (user.get().getIsOnline() == 0) {
            throw new InvalidInputException(username + " is offline");
        }

        List<Neighbor> allCountries = neighborRepository.findAll();

        for (Neighbor neighbor : allCountries) {
            if (neighbor.getStartingCountryName().equalsIgnoreCase(travelRequest.getStartingCountryName())) {
                if (travelRequest.getBudgetPerCountry() == null ||
                        travelRequest.getBudgetPerCountry().compareTo(BigDecimal.ZERO) < 0) {
                    throw new InvalidInputException("The budget per country must be at least 0");
                }

                if (travelRequest.getTotalBudget() == null ||
                        travelRequest.getTotalBudget().compareTo(BigDecimal.ZERO) < 0) {

                    throw new InvalidInputException("The total budget must be at least 0");
                }

                if (travelRequest.getBudgetPerCountry().compareTo(travelRequest.getTotalBudget()) > 0){
                    throw new InvalidInputException("The budget per country must be less or equal to total budget");
                }

                return convertCurrency(username, travelRequest);
            }
        }
        throw new InvalidInputException("There are no country with this name");
    }

    @Override
    public StringBuilder convertCurrency(String username, TravelRequest travelRequest) throws JsonProcessingException {

        Map<String, Double> rates = getRates(); // currency_abrev and rates

        List<Country> countries = countryRepository.findAll();

        Map<String, String> currencyAbrevs = new HashMap<>(); // country_name and currency_abrev

        for (Country country : countries) {
            currencyAbrevs.put(country.getCountryName(), country.getCurrencyAbrev());
        }

        List<Neighbor> neighbors = neighborRepository.findByStartingCountryName(travelRequest.getStartingCountryName()); // neighbors country name

        Map<String, BigDecimal> convertedMoney = new HashMap<>();

        for (Neighbor neighbor : neighbors) {

            if (currencyAbrevs.containsKey(neighbor.getNeighborCountryName())) {

                if (!currencyAbrevs.get(neighbor.getNeighborCountryName()).equalsIgnoreCase("EUR")) {

                    BigDecimal convert = travelRequest.getBudgetPerCountry().multiply(BigDecimal.valueOf((rates.get(currencyAbrevs.get(neighbor.getNeighborCountryName())))));
                    convertedMoney.put(currencyAbrevs.get(neighbor.getNeighborCountryName()), convert);
                } else {
                    convertedMoney.put(currencyAbrevs.get(neighbor.getNeighborCountryName()), travelRequest.getBudgetPerCountry());
                }
            }
        }

        int countTours = calculateCountOfVisits(travelRequest);
        int countNeighbors = neighbors.size();
        BigDecimal moneyPerOneTour = travelRequest.getBudgetPerCountry().multiply(new BigDecimal(String.valueOf(countNeighbors)));
        int leftMoney = travelRequest.getTotalBudget().subtract(moneyPerOneTour.multiply(BigDecimal.valueOf(countTours))).intValue();

        List<String> neighborsName = new ArrayList<>();
        for (Neighbor neighbor : neighbors) {
            neighborsName.add(neighbor.getNeighborCountryName());
        }
        return outputBuilder(username, travelRequest, currencyAbrevs, neighborsName, convertedMoney, countTours, leftMoney);
    }

    private StringBuilder outputBuilder(String username, TravelRequest travelRequest, Map<String, String> currencyAbrevs,
                                        List<String> neighbors, Map<String, BigDecimal> convertedMoney,
                                        int countTours, int leftMoney) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(travelRequest.getStartingCountryName())
                .append(" has ")
                .append(neighbors.size()) // count neighbors
                .append(" neighbor countries ")
                .append(neighbors)
                .append(" and ")
                .append(username)
                .append(" can travel around them ")
                .append(countTours)
                .append(" times. ")
                .append("He will have ")
                .append(leftMoney)
                .append(" EUR leftover. ")
                .append("For ");

        for (int i = 0; i < neighbors.size(); i++) {
            if (convertedMoney.containsKey(currencyAbrevs.get(neighbors.get(i)))) {

                stringBuilder
                        .append(neighbors.get(i))
                        .append(" he will need to buy ")
                        .append(convertedMoney.get(currencyAbrevs.get(neighbors.get(i))))// money
                        .append(" ")
                        .append(currencyAbrevs.get(neighbors.get(i))); // currency
            }

            if (i != neighbors.size() - 1) {
                stringBuilder
                        .append(", for ");
            } else {
                stringBuilder.append(".");
            }
        }
        return stringBuilder;
    }

    @Override
    public int calculateCountOfVisits(TravelRequest travelRequest) {

        if (travelRequest.getTotalBudget() != null
                && travelRequest.getStartingCountryName() != null
                && travelRequest.getBudgetPerCountry() != null) {

            List<Neighbor> countNeighbors = neighborRepository.findByStartingCountryName(travelRequest.getStartingCountryName());

            BigDecimal moneyPerOneTour = travelRequest.getBudgetPerCountry().multiply(new BigDecimal(String.valueOf(countNeighbors.size())));

            return travelRequest.getTotalBudget().divide(moneyPerOneTour).intValue();
        }
        throw new InvalidInputException("Invalid input!");
    }
}