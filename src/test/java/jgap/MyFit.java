package jgap;

//import org.jgap.FitnessFunction;
//import org.jgap.Gene;
//import org.jgap.IChromosome;
//import org.jgap.impl.BooleanGene;
//
//import com.sx.mmt.internal.util.SimpleBytes;
//
//public class MyFit extends FitnessFunction{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -6125667967846971053L;
//	private int[] target=new int[]{1,2,3,4,4,3,4,5,6,2,4,5,7,8,9,6,5,3,2,5,6,0,6,7,6,7,4,2,4};
//	@Override
//	protected double evaluate(IChromosome a_subject) {
//		Gene[] genes = a_subject.getGenes();
//		int[] bit=new int[genes.length];
//		for(int i=0;i<genes.length;i++){
//			bit[i]=((BooleanGene)genes[i]).booleanValue()?1:0;
//		}
//		int size=new SimpleBytes(bit).toInt();
//		if(size>target.length-1){
//			return 0;
//		}else{
//			return Integer.MAX_VALUE-target[size];
//		}
//		
//	}
//	
//}
