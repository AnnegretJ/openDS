/*

*  This file is part of OpenDS (Open Source Driving Simulator).
*  Copyright (C) 2021 Rafael Math
*
*  OpenDS is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*
*  OpenDS is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  You should have received a copy of the GNU General Public License
*  along with OpenDS. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.opends.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.AmazonPollyException;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.polly.model.VoiceId;

import eu.opends.drivingTask.settings.SettingsLoader;
import eu.opends.drivingTask.settings.SettingsLoader.Setting;
import eu.opends.main.Simulator;
import eu.opends.tools.Util;


public class AmazonPollyTTS 
{
    private AmazonPollyClient pollyClient;
    private Voice voice = null;
    private boolean isEnabled = false;
    

    public AmazonPollyTTS()
    {
    	SettingsLoader settingsLoader = Simulator.getSettingsLoader();
    	isEnabled = settingsLoader.getSetting(Setting.AmazonPolly_enableConnection, false);
    	
    	if(isEnabled)
    	{
    		String accessKey = settingsLoader.getSetting(Setting.AmazonPolly_accessKey, "");
    		String secretKey = settingsLoader.getSetting(Setting.AmazonPolly_secretKey, "");
        	Regions regionID = getRegionById(settingsLoader.getSetting(Setting.AmazonPolly_region, "US_EAST_1"));
        	VoiceId voiceID = getVoiceById(settingsLoader.getSetting(Setting.AmazonPolly_voice, "Joanna"));

        	AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        	pollyClient = new AmazonPollyClient(credentials);
        	pollyClient.setRegion(Region.getRegion(regionID));
        
        	// create describe voices request
        	DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        	try {
        		// synchronously ask Amazon Polly to describe available TTS voices
        		DescribeVoicesResult describeVoicesResult = pollyClient.describeVoices(describeVoicesRequest);

        		// find voice by ID
        		for(Voice v : describeVoicesResult.getVoices())
        		{
        			//System.out.println("Voice: " + v.toString());
        			if(v.getName().equals(voiceID.toString()))
        				voice = v;
        		}

        		if(voice == null)
        			System.err.println("AmazonPollyTTS: voice '" + voiceID.toString() + 
        					"' could not be initialized");
        		
        	}
        	catch (AmazonPollyException e1) {
        		System.err.println("AmazonPollyTTS: the access key is not authorized to use Amazon Polly");
        	}
        	catch (SdkClientException e2) {
        		System.err.println("AmazonPollyTTS: unable to connect to Amazon Web Services (Amazon Polly)");
        	}
    	}
    }

    
	private VoiceId getVoiceById(String voiceId)
	{
	    for(VoiceId v : VoiceId.values())
	    {
	        if(v.toString().equalsIgnoreCase(voiceId))
	            return v;
	    }
	    
	    System.err.println("AmazonPollyTTS: invalid voice ID '" + voiceId +
	    		"'. Set voice ID to default: 'Joanna'");
	    return VoiceId.Joanna;
	}
	
	
	private Regions getRegionById(String regionId)
	{
	    for(Regions r : Regions.values())
	    {
	        if(r.toString().equalsIgnoreCase(regionId))
	            return r;
	    }
	    
	    System.err.println("AmazonPollyTTS: invalid region ID '" + regionId +
	    		"'. Set region ID to default: 'US_EAST_1'");
	    return Regions.US_EAST_1;
	}
	
    
    public boolean isEnabled() 
    {
    	return isEnabled;
    }
    
    
    public boolean synthesize(String text, OutputFormat format, String fileName) 
    {
    	if(voice != null)
    	{
    		try {
    			SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text)
    					.withVoiceId(voice.getId()).withOutputFormat(format);
        
    			SynthesizeSpeechResult synthRes = pollyClient.synthesizeSpeech(synthReq);

    			InputStream inputStream = synthRes.getAudioStream();
    			boolean savedSuccessfully = saveToFile(inputStream, fileName);
    			inputStream.close();
    			
        		return savedSuccessfully;
    		}
    		catch(Exception e) {
    			System.err.println("AmazonPollyTTS: error while attempting to synthesize speech");
    		}
    	}
    	
    	return false;
    }
    

    private boolean saveToFile(InputStream inputStream, String filePath)
    {
    	try {
    		
    		// the file path is relative to the assets folder 
    		File file = new File("assets/" + filePath);
    		
    		// make sure that the target directory exists
    		String folderName = file.getParent();    		
    		Util.makeDirectory(folderName);
    		
    		OutputStream outStream = new FileOutputStream(file);

    		byte[] buffer = new byte[8 * 1024];
    		int bytesRead;
    		while ((bytesRead = inputStream.read(buffer)) != -1)
    			outStream.write(buffer, 0, bytesRead);
    	
    		outStream.close();
    		
    		return true;    	
    	}
		catch(IOException e) {
			System.err.println("AmazonPollyTTS: unable to save audio file '" + filePath + "'");
		}
    	
    	return false;
    }

}