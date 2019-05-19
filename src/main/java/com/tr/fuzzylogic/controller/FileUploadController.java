package com.tr.fuzzylogic.controller;

import static com.tr.fuzzymatcher.domain.ElementType.ADDRESS;
import static com.tr.fuzzymatcher.domain.ElementType.NAME;
import static com.tr.fuzzymatcher.domain.ElementType.TEXT;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tr.fuzzylogic.model.Employee;
import com.tr.fuzzymatcher.component.MatchService;
import com.tr.fuzzymatcher.domain.Document;
import com.tr.fuzzymatcher.domain.Element;
import com.tr.fuzzymatcher.domain.Match;

/**
 * Handles requests for the application file upload requests
 */
@Controller
public class FileUploadController {
	private static final Logger logger = LoggerFactory
			.getLogger(FileUploadController.class);
	 Employee sample=null;


	 @RequestMapping(value = "/readjson", method = RequestMethod.POST)
	 public @ResponseBody
	 Map<Document, List<Match<Document>>> uploadFileHandler() throws IOException, ParseException {
		 String legalEntityIdentifier=null;
		 String legalOrgName=null,TickerCode=null,Industry=null,CUSIP=null,ISIN=null,BusinessRegistrycode=null;
		 String country=null,LegalForm=null,RegisteredAddress=null,ExchangeName=null,TradesAs=null;
		 JSONParser parser = new JSONParser();
		 JSONArray a = (JSONArray) parser.parse(new FileReader("C:\\Users\\Sumukh\\Downloads\\Compressed\\C&M_Json_File\\Fuzzy_Matching_Test_Data.json"));
		 List<Document> inputData = new ArrayList<>();
		 int i=1;
		  for (Object o : a)
		  {
			  
			  String input="";
		    JSONObject data = (JSONObject) o;
		    legalEntityIdentifier = (String) data.get("Legal Entity Identifier");
		    legalOrgName = (String) data.get("Legal Org Name");
		    country = (String) data.get("Country");
		    LegalForm = (String) data.get("Legal Form");
		    RegisteredAddress = (String) data.get("Registered Address");
		    ExchangeName = (String) data.get("Exchange Name");
		    TradesAs = (String) data.get("Trades As");
		    TickerCode = (String) data.get("Ticker Code");
		    Industry = (String) data.get("Industry");
		    CUSIP = (String) data.get("CUSIP");
		    ISIN = (String) data.get("ISIN");
		    BusinessRegistrycode = (String) data.get("Business Registry code");
		     String inputa=input+i;
		     i++;
		        inputData.add(new Document.Builder(inputa)
		                .addElement(new Element.Builder().setType(NAME).setValue(legalEntityIdentifier).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(legalOrgName).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(country).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(LegalForm).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(RegisteredAddress).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(ExchangeName).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(TradesAs).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(TickerCode).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(Industry).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(CUSIP).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(ISIN).createElement())
		                .addElement(new Element.Builder().setType(NAME).setValue(BusinessRegistrycode).createElement())
		                .createDocument());
			   }
		  //System.out.println("---------------inputData-----------------"+inputData);
		  JSONArray outputfile = (JSONArray) parser.parse(new FileReader("C:\\Users\\Sumukh\\Downloads\\Compressed\\C&M_Json_File\\Alpha.json"));
		  List<Document> outputData = new ArrayList<>();
		  int j=1;
		  for (Object o : outputfile)
		  {
			  
			  String inputs="";
		    JSONObject data = (JSONObject) o;
		    legalEntityIdentifier = (String) data.get("Legal Entity Identifier");
		    legalOrgName = (String) data.get("Legal Org Name");
		    country = (String) data.get("Country");
		    LegalForm = (String) data.get("Legal Form");
		    RegisteredAddress = (String) data.get("Registered Address");
		    String inp=inputs+j;
		    j++;
		  outputData.add(new Document.Builder(inp)
	                .addElement(new Element.Builder().setType(NAME).setValue(legalEntityIdentifier).createElement())
	                .addElement(new Element.Builder().setType(NAME).setValue(legalOrgName).createElement())
	                .addElement(new Element.Builder().setType(NAME).setValue(country).createElement())
	                .addElement(new Element.Builder().setType(NAME).setValue(LegalForm).createElement())
	                .addElement(new Element.Builder().setType(NAME).setValue(RegisteredAddress).createElement())

	                .createDocument());
		  }
		 // System.out.println("-----------------outputData----------------"+outputData);
		 // System.out.println("inputData"+inputData);
		 // System.out.println("outputData"+outputData);
		
	        MatchService matchService = new MatchService();
	         Map<Document, List<Match<Document>>> result = matchService.applyMatch(inputData, outputData);
	        result.entrySet().forEach(entry -> {
	             entry.getValue().forEach(match -> {
	                 System.out.println("Data: " + match.getData() + " Matched With: " + match.getMatchedWith() + " Score: " + match.getScore().getResult());
	             });
	         });
			return result;
		 
		
	 
	 }}
	