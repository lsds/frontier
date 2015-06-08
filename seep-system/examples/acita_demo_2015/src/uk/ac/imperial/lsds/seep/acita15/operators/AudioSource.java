/*******************************************************************************
 * Copyright (c) 2014 Imperial College London
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Raul Castro Fernandez - initial API and implementation
 ******************************************************************************/
package uk.ac.imperial.lsds.seep.acita15.operators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import uk.ac.imperial.lsds.seep.GLOBALS;
import uk.ac.imperial.lsds.seep.comm.serialization.DataTuple;
import uk.ac.imperial.lsds.seep.comm.serialization.messages.TuplePayload;
import uk.ac.imperial.lsds.seep.operator.StatelessOperator;


public class AudioSource implements StatelessOperator {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AudioSource.class);
	private static String[] audioFiles = new String[]{"my_name_is_dan.wav", "my_name_is_peter.wav", "my_name_is_theodoros.wav", "my_name_is_bob.wav", "testing_one_two.wav"};
	private static final String audioDir = "/tmp/audio";
	private int audioIndex = 0;
	
	public void setUp() {
		System.out.println("Setting up AUDIO_SOURCE operator with id="+api.getOperatorId());
		
	}

	public void processData(DataTuple dt) {
		Map<String, Integer> mapper = api.getDataMapper();
		DataTuple data = new DataTuple(mapper, new TuplePayload());
	
		byte[] audioClip = null;
		try
		{
			audioClip = Files.readAllBytes(Paths.get(audioDir + "/" + audioFiles[audioIndex]));
			audioIndex++;
		} catch(IOException e)
		{
			logger.error("Problem opening audio file: ", e);
			System.exit(1);
		}
		//InputStream stream = new FileInputStream(audio);
		//InputStream stream = GLOBALS.class
				//.getResourceAsStream("10001-90210-01803.wav");
				//.getResourceAsStream("/edu/cmu/sphinx/demo/aligner/10001-90210-01803.wav");
		
		long tupleId = 0;
		
		boolean sendIndefinitely = Boolean.parseBoolean(GLOBALS.valueFor("sendIndefinitely"));
		long numTuples = Long.parseLong(GLOBALS.valueFor("numTuples"));
		int tupleSizeChars = Integer.parseInt(GLOBALS.valueFor("tupleSizeChars"));
		boolean rateLimitSrc = Boolean.parseBoolean(GLOBALS.valueFor("rateLimitSrc"));
		long frameRate = Long.parseLong(GLOBALS.valueFor("frameRate"));
		long interFrameDelay = 1000 / frameRate;
		logger.info("Source inter-frame delay="+interFrameDelay);
		
		//final String value = generateFrame(tupleSizeChars);
		final long tStart = System.currentTimeMillis();
		logger.info("Source sending started at t="+tStart);
		logger.info("Source sending started at t="+tStart);
		logger.info("Source sending started at t="+tStart);
		while(sendIndefinitely || tupleId < numTuples){
			
			DataTuple output = data.newTuple(tupleId, audioClip);
			output.getPayload().timestamp = tupleId;
			if (tupleId % 1000 == 0)
			{
				logger.info("Source sending tuple id="+tupleId+",t="+output.getPayload().instrumentation_ts);
			}
			else
			{
				logger.debug("Source sending tuple id="+tupleId+",t="+output.getPayload().instrumentation_ts);
			}
			api.send_highestWeight(output);
			
			tupleId++;
			
			long tNext = tStart + (tupleId * interFrameDelay);
			long tNow = System.currentTimeMillis();
			if (tNext > tNow && rateLimitSrc)
			{
				logger.debug("Source wait to send next frame="+(tNext-tNow));
				try {
					Thread.sleep(tNext - tNow);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}
		System.exit(0);
	}
	
	public void processData(List<DataTuple> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private String generateFrame(int tupleSizeChars)
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tupleSizeChars; i++)
		{
			builder.append('x');
		}
		return builder.toString();
	}
}