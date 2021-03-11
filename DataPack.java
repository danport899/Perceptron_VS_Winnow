package Project1;

import java.math.BigDecimal;
import java.util.List;

public class DataPack {

	
	int iterationCount;
	double accuracy;
	List<BigDecimal> weights;
	
	
	public DataPack(int itCount, double acc , List<BigDecimal> w) {
		iterationCount= itCount;
		accuracy = acc;
		weights = w;
	}

	public void printWeights() {
		byte counter =0;
		for(BigDecimal bd: weights) {
			System.out.print("(" + bd + "x"+counter + ") +");
			counter++;
		}
		System.out.println();
	}
}
