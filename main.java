package Project1;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class main {
	
	static final byte totalAttributes = 10;
	static final long instanceSpace = (long) Math.pow(2, totalAttributes);
	
	static final int relevantAttCount = 3;
	static double trainingTestingPercentage = .1;
	static final int runCap = 50;
	static final int maxIterations = 100;
	
	
	static final boolean runningPerceptron = false;
	static final boolean runningBoth = true;
	
	static final double minAccuracy = 1;
	
	static List<Car> totalSet = new ArrayList<Car>();
	static List<Car> trainingSet = new ArrayList<Car>();
	static List<Car> validationSet = new ArrayList<Car>();

	
	public static void main(String[] args) {
		
		initInstanceSet();
		Collections.shuffle(totalSet);	
		initTestingSet();
		initExampleDivision();
		
		int totalRuns = 0;
		int epochCount;
		int prevEpochCount;
		BigDecimal decrement = new BigDecimal(0.1);
		//double accuracy = 0;
		
		//Learning rate must be between (0,1]
		BigDecimal learningRate = new BigDecimal(1.0);
		//coAlpha must be larger than one;
		BigDecimal coAlpha = new BigDecimal(2.0);
		
		DataPack data, primaryData = null;
		

		if(runningBoth) {
			
			int[] totalPercepEpochs = {0,0,0,0,0,0,0};
			int[] totalWINEpochs = {0,0,0,0,0,0,0};
			double[] bestCoAlphas = {0,0,0,0,0,0,0};
			double[] bestLearningRates = {0,0,0,0,0,0,0};
			double[] percepAccuracy = {0,0,0,0,0,0,0};
			double[] WINAccuracy = {0,0,0,0,0,0,0};
			
			while(totalRuns < runCap) {	
				initInstanceSet();
				Collections.shuffle(totalSet);			
				initTestingSet();		
				trainingSet.removeAll(trainingSet);
				
				while(trainingTestingPercentage <= .7) {
					learningRate = new BigDecimal(1);
					coAlpha = new BigDecimal(2);
					
					initExampleDivision();
					prevEpochCount = 0;
					epochCount = 0;
					prevEpochCount += maxIterations;
					epochCount += maxIterations;
					decrement = new BigDecimal(0.1);
					
					int smallestEpoch = maxIterations+1;
					double bestLearningRate = 1;
					
					do {
						prevEpochCount = epochCount;
						//System.out.println(learningRate);
						data = perceptron(learningRate);
						epochCount = (data == null) ? maxIterations:data.iterationCount;
						if(epochCount < smallestEpoch) {
							smallestEpoch = epochCount;
							bestLearningRate = learningRate.doubleValue();
							primaryData = data;
						}
						learningRate = learningRate.subtract(decrement,MathContext.DECIMAL32);
						//System.out.println("prevCount: " + prevEpochCount);
						//System.out.println("Current Count: " + epochCount);
						if(learningRate.doubleValue() <= decrement.doubleValue()) decrement = decrement.divide(new BigDecimal(10));
						
					}
					while(learningRate.doubleValue() >= 0.1);
					
					totalPercepEpochs[(int) (trainingTestingPercentage * 10 -1)] += smallestEpoch;
					bestLearningRates[(int) (trainingTestingPercentage * 10 -1)] += bestLearningRate;
					percepAccuracy[(int) (trainingTestingPercentage * 10 -1)] += primaryData.accuracy;
					primaryData.printWeights();
					
					System.out.println();		
					System.out.println("Training Size : " + trainingTestingPercentage*100 + "%");
					System.out.println("Training Set Size: " + trainingSet.size());
					System.out.println("Perceptron Total Visited: " + smallestEpoch * trainingSet.size());
					System.out.println("Accuracy : " + primaryData.accuracy);
					//primaryData.printWeights();
					
					decrement = new BigDecimal(0.1); 
					prevEpochCount = 0;
					epochCount = 0;
					prevEpochCount += maxIterations;
					epochCount += maxIterations;
					
					
					smallestEpoch = maxIterations +1;
					double bestCoAlpha = 2;
					
					do {
					//System.out.println(coAlpha);
					prevEpochCount = epochCount;
					data = WINNOW(coAlpha);
					epochCount = (data == null) ? maxIterations:data.iterationCount;
			
					if(epochCount < smallestEpoch) {
						smallestEpoch = epochCount;
						bestCoAlpha = coAlpha.doubleValue();
						primaryData = data;
	
					}
					coAlpha = coAlpha.subtract(decrement,MathContext.DECIMAL32);
					if(coAlpha.doubleValue() <= 1.0 +decrement.doubleValue()) decrement = decrement.divide(new BigDecimal(10));	
					//System.out.println(coAlpha + ": " +epochCount);
					//if(smallestEpoch < maxIterations && epochCount == maxIterations) break;
					}
		
					while(coAlpha.doubleValue() > 1.0001);
					
					totalWINEpochs[(int) (trainingTestingPercentage * 10 -1)] += smallestEpoch;
					bestCoAlphas[(int) (trainingTestingPercentage * 10 -1)] += bestCoAlpha;
					WINAccuracy[(int) (trainingTestingPercentage * 10 -1)] += primaryData.accuracy;
					primaryData.printWeights();
	
					System.out.println("WINNOW Total Visited: " + smallestEpoch * trainingSet.size());
					System.out.println("Accuracy : " + primaryData.accuracy);
					//primaryData.printWeights();
					trainingTestingPercentage += 0.1;
				}
				trainingTestingPercentage = 0.1;
				totalRuns++;
			}
			for(int i=0 ; i < totalPercepEpochs.length; i ++ ) {
				System.out.println();
				
				System.out.println("Average Epochs at " + (i+1) *10 + "%: ");
				
				System.out.println("Perceptron:" + totalPercepEpochs[i]/(float)runCap);
				System.out.println("Average Best learningRate:" + bestLearningRates[i]/runCap);
				System.out.println("Average Accuracy:" + percepAccuracy[i]/runCap);
				
				System.out.println("WINNOW:" +totalWINEpochs[i]/(float)runCap);
				System.out.println("Average Best coAlpha:" +bestCoAlphas[i]/runCap);
				System.out.println("Average Accuracy:" + WINAccuracy[i]/runCap);
				
			}
			
		}
		else if (runningPerceptron) {
			perceptron(learningRate);
		}
		else {
			WINNOW(coAlpha);
		}
		
		//System.out.println("Average Iterations = " + (double)iterationCount/100.0 );
		//System.out.println("Average Epochs = " + (double)(epochCount)/100.0 );
			
		
		
		System.out.println("Validation Set Size: " + validationSet.size());
	}
	
	public static boolean classifierChecklist(byte[] byteArray) {
		
		double w0, w1, w2, w3 , w4 , w5 , w6, w7 , w8 , w9;
		switch(relevantAttCount) {
			case 1:
				 w0 = 2; w1 = 0; w2 = 0; w3 = 0; w4 = 0; w5 = 0; w6 = 0; w7 = 0; w8 = 0; w9 = 0;
				break;
			case 2:
				w0 = 20; w1 = -10; w2 = 0; w3 = 0; w4 = 0; w5 = 0; w6 = 0; w7 = 0; w8 = 0; w9 = 0;
				break;
			case 3:
				w0 = 19.33; w1 = 0; w2 = 0; w3 = 0; w4 = 0; w5 = 0; w6 = -14.23; w7 = 0; w8 = 0; w9 = -3.73;
				break;
			case 4:
				w0 = 0; w1 = 0; w2 = -8.6; w3 = 0; w4 = 8.53; w5 = 5.1; w6 = 0; w7 = -3.42; w8 = 0; w9 = 0;
				break;
			case 5:
				w0 = 0; w1 = -3; w2 = 0; w3 = 0; w4 = 10.53; w5 = 5.1; w6 = 0; w7 = -8.42; w8 = 0; w9 = -2.5;
				break;
			case 6:
				w0 = 0; w1 = -3.7; w2 = 4; w3 = 0; w4 = 3.53; w5 = 2.1; w6 = 0; w7 = -3.42; w8 = 0; w9 = -2.5;
				break;
			case 7:
				w0 =1.5; w1 = -4.7; w2 = 4; w3 = 0; w4 = 3.53; w5 = 2.1; w6 = 0; w7 = -3.42; w8 = 0; w9 = -3.1;
				break;
			case 8:
				w0 =1.5; w1 = -4.7; w2 = 3; w3 = 0; w4 = 3.53; w5 = 2.1; w6 = 0; w7 = -5.42; w8 = 3.3; w9 = -3.1;
				break;
			case 9:
				w0 = 19.5; w1 = -3; w2 = -8; w3 = 4.8; w4 = 17.8; w5 = -13.2; w6 = 0; w7 = -6.42; w8 = 0; w9 = -10.43;
				break;
			case 10:
				w0 = 19.5; w1 = -3; w2 = -18.4; w3 = 7.8; w4 = 19; w5 = -13.2; w6 = -5.2; w7 = -6.42; w8 = 10.53; w9 = -10.43;
				break;
			default:
				w0 = 2; w1 = 0; w2 = 0; w3 = 0; w4 = 0; w5 = 0; w6 = 0; w7 = 0; w8 = 0; w9 = 0;
			 
		}
		double value = (w0*byteArray[0] + w1*byteArray[1] + w2*byteArray[2] + w3*byteArray[3] + w4*byteArray[4] + w5*byteArray[5] 
				+ w6*byteArray[6] + w7*byteArray[7] + w8*byteArray[8] + w9*byteArray[9]);
		
		
		
		boolean classifier = ( value > 0) ? true :false;
		
		if(classifier) {
			return true;
		}
		return false;
	}
	
	public static void initInstanceSet(){
		int totalPositive = 0;
		
	    int boolArraySize =  totalAttributes *2;
		
		for(int i = 0; i < instanceSpace; i ++) {
			
			String bin = Integer.toBinaryString(i);
            while (bin.length() < totalAttributes)
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
          
            byte[] byteArray = new byte[boolArraySize];
            for (int j = 0; j < chars.length; j++) {
                byteArray[j] = (byte) (chars[j] == '0' ? 0 : 1);
            }
             
            for(int k = 0; k < chars.length; k++) {
            		byteArray[k+totalAttributes] = (byte) ((byteArray[k] == (byte) 0) ? 1 : 0);   	
            }
         
            Car nextCombination = new Car(byteArray);
    		//add artificial attribute at the end
    		
            if(classifierChecklist(byteArray)) {
            	nextCombination.classification = true;
            	totalPositive++;
            }
            totalSet.add(nextCombination);
            
		}
		System.out.println("Total Positive Examples: " + totalPositive);
		
	}
	
	public static void initTestingSet() {
		
		
		
		validationSet.removeAll(validationSet);
		
		for(int m = (int) instanceSpace-1; m > (int)(instanceSpace * .8); m--) {
			validationSet.add(totalSet.get(m));
			totalSet.remove(m);
		}
	}

	public static void initExampleDivision() {
		
	
		int splitOff = (int)(instanceSpace * trainingTestingPercentage) - trainingSet.size();
		int positiveCount = 0, negativeCount = 0;
		int counter = totalSet.size()-1;
		
		while(trainingSet.size() != (int)(instanceSpace * trainingTestingPercentage)) {
	
			if(positiveCount == splitOff/2 && negativeCount == splitOff/2) break;
			if((totalSet.get(counter).classification && positiveCount == splitOff/2)
				|| (!totalSet.get(counter).classification && negativeCount == splitOff/2) ) {
			}
			else if(totalSet.get(counter).classification) {
				positiveCount++;
				trainingSet.add(totalSet.get(counter));
				totalSet.remove(counter);
			}
			else {
				negativeCount++;
				trainingSet.add(totalSet.get(counter));
				totalSet.remove(counter);
			}
			counter--;

		}
		
		
		positiveCount = 0;
		for(Car c: trainingSet) {
			if(c.classification) positiveCount++;
		}
		System.out.println("Training Set Size: " + trainingSet.size());
		System.out.println("Positives in Training: " + positiveCount);
		//compareLists(trainingSet, validationSet);
		
	}

	public static DataPack perceptron(BigDecimal learningRate) {
		
		List<BigDecimal> weights = generateWeights(false);
		DataPack data;
	
		double accuracy = 0;
		BigDecimal prediction = new BigDecimal(0);
		int iterationCount = 0;
		
		while(accuracy < minAccuracy) {
			accuracy = 0;
			
			iterationCount++;
			
			for(Car c: trainingSet) {
				prediction = new BigDecimal(0);
				int classification = c.classification ? 1 : 0;
				for(int i = 0; i < totalAttributes; i++) {
					if(c.attributes[i] ==1) {
						prediction = prediction.add(weights.get(i));
					}
					//System.out.print("(" + weights.get(i).doubleValue() + " * " + c.attributes[i] + ") +");		
				}
				
				prediction = prediction.add(weights.get(weights.size()-1));
				//System.out.print("(" + weights.get(weights.size()-1) + ") +");
				//System.out.print("= " + prediction);
				if(prediction.doubleValue() > 0) prediction = new BigDecimal(1);
				else prediction = new BigDecimal(0);
				//System.out.println(" = " + prediction);
				//System.out.println(" Actual value =" + classification);
				
				if(prediction.doubleValue() != classification) {
					
					for(int i = 0; i < totalAttributes; i++) {
						if(c.attributes[i] == 1) {
							 BigDecimal newWeight = weights.get(i);
							 BigDecimal change =  new BigDecimal(learningRate.doubleValue()*(classification - prediction.doubleValue()));
							 //System.out.println(change);
							 newWeight = newWeight.add(change,MathContext.DECIMAL32);
							 //System.out.println(newWeight);
							 weights.set(i, newWeight);
						}
					}
					 BigDecimal newWeight = weights.get(weights.size()-1);
					 BigDecimal change =  new BigDecimal(learningRate.doubleValue()*(classification - prediction.doubleValue()));
					 newWeight = newWeight.add(change,MathContext.DECIMAL32);
					 weights.set(weights.size()-1, newWeight);
				}
				else {
					//System.out.println("No changes");
					accuracy++;
				}
			}
			
			//System.out.println(accuracy + "/" + trainingSet.size());
			accuracy = accuracy/(double) trainingSet.size();
			
			if(iterationCount == maxIterations) return null;
		}
		
		accuracy = validateWeights(weights, 0);
		
		data = new DataPack(iterationCount, accuracy, weights );
		
		return data;
		//System.out.println("TOTAL ITERATIONS = " + iterationCount);
		
		
	}
	
	public static DataPack WINNOW(BigDecimal coAlpha) {
		
		List<BigDecimal> weights = generateWeights(true);
		DataPack data;
		
		double accuracy = 0;
		BigDecimal prediction;
		int iterationCount = 0;
		double threshhold = totalAttributes*2-.1;
			
		while(accuracy < minAccuracy) {
			accuracy = 0;	
			iterationCount++;
			for(Car c: trainingSet) {
				prediction = new BigDecimal(0);
				int classification = c.classification ? 1 : 0;
				for(int i = 0; i < totalAttributes *2 ; i++) {
					if(c.attributes[i] == 1) {
						prediction = prediction.add(weights.get(i),MathContext.DECIMAL32);
						//System.out.print("(" + weights.get(i) +") +");
					}
					
				}
				//System.out.print("(" + weights.get(weights.size()-1) +") +");
				//System.out.print("= " + prediction);
				if(prediction.doubleValue() > threshhold) prediction = new BigDecimal (1);
				else prediction = new BigDecimal (0);
				//System.out.println(" = " + prediction);
				//System.out.println(" Actual value =" + classification);
				
				if(prediction.doubleValue() != classification) {
					//System.out.println("Changing weights");
					for(int k = 0; k < totalAttributes *2; k++) {
						if(c.attributes[k] ==1) {
							 BigDecimal newWeight = weights.get(k);
							 if(classification < prediction.doubleValue()) newWeight = newWeight.divide(coAlpha,MathContext.DECIMAL32);
							 else newWeight = newWeight.multiply(coAlpha,MathContext.DECIMAL32 );
							
							 weights.set(k, newWeight);
						}
					}
					
					
				}
				else accuracy++;
			}
			
			accuracy = accuracy/(double) trainingSet.size();
			if (iterationCount == maxIterations) return null;
		}
		
		accuracy = validateWeights(weights, threshhold);
		
		return data = new DataPack(iterationCount, accuracy, weights);
		//System.out.println("TOTAL ITERATIONS = " + iterationCount);
		
		//return accuracy/(double) validationSet.size();
		

	}
	
	
	public static List<BigDecimal> generateWeights(boolean weightValue){
		
		List<BigDecimal> weights = new ArrayList<BigDecimal>();
		
		if(weightValue) {
			for(int i = 0; i < totalAttributes * 2; i ++) {
				BigDecimal one = new BigDecimal(1);
				weights.add(one);
			}
		}
		else {
			for(int i = 0; i < totalAttributes + 1; i ++) {
				BigDecimal zero = new BigDecimal(0);
				weights.add(zero);
			}
		}
		/*
		//Fill list with RNG weight plus one artificial attribute
		for(int i = 0; i < totalAttributes + 1; i ++) {
			double randomWeight =  Math.round(Math.random() * 10.0) / 10.0;
			if (randomWeight == 0) randomWeight += 0.1;
			weights.add(randomWeight);
		}
		*/
		return weights;
	}

	public static double validateWeights(List<BigDecimal> weights, double threshold) {
		double prediction = 0.0;
		int accuracy = 0;
		for(Car c: validationSet) {
			int classification = c.classification ? 1 : 0;
			for(int i = 0; i < weights.size(); i++) {
				byte value;
				if(i == weights.size() -1 && threshold ==0) value = 1;
				else value= c.attributes[i];
				
				prediction += weights.get(i).doubleValue() * value;
				//System.out.print("(" + weights.get(i) + "*" + value + ") +");
			}
			
			//System.out.print("= " + prediction);
			if(prediction > threshold) prediction = 1;
			else prediction = 0;
			//System.out.println(" = " + prediction);
			//System.out.println(" Actual value =" + classification);
			
			if(prediction == classification) {
				 accuracy++;
			}
			
		}
		
		return accuracy/(double)validationSet.size();
	}

	public static boolean compareLists(List<Car> trainingSet, List<Car> validationSet) {	
		
		for(Car c1: trainingSet) {
			for (Car c2: validationSet) {
				if(c1.attributes.equals(c2.attributes)) return true;;
			}
		}
		return false;
	}
}
