package com.shru.coronavirustracker.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.shru.coronavirustracker.model.LocationDetails;

@Service
public class CoronavirusDataService {
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationDetails> allDetails = new ArrayList<LocationDetails>();
	@PostConstruct
	@Scheduled(cron="* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
	   List<LocationDetails> newDetails = new ArrayList<LocationDetails>();
		HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());		
		StringReader csvBodyReader = new StringReader(httpResponse.body()); 
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationDetails localDetails	= new LocationDetails();
			localDetails.setCountry(record.get("Country/Region"));
			localDetails.setState(record.get("Province/State"));
			localDetails.setLatestTotalCases(Integer.parseInt(record.get(record.size() -1)));
			System.out.println(localDetails);
			newDetails.add(localDetails);
		}
		    this.allDetails= newDetails;
	}
	
}
