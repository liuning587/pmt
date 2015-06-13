package jgap;

//import org.jgap.Chromosome;
//import org.jgap.Configuration;
//import org.jgap.DefaultFitnessEvaluator;
//import org.jgap.Gene;
//import org.jgap.IChromosome;
//import org.jgap.InvalidConfigurationException;
//import org.jgap.event.EventManager;
//import org.jgap.impl.BestChromosomesSelector;
//import org.jgap.impl.BooleanGene;
//import org.jgap.impl.ChromosomePool;
//import org.jgap.impl.GreedyCrossover;
//import org.jgap.impl.StockRandomGenerator;
//import org.jgap.impl.SwappingMutationOperator;
//
//public class Choose {
//	
//	public IChromosome createChromosome(Configuration conf){
//		try{
//			Gene[] genes = new Gene[8];
//			for(int i=0;i<genes.length;i++){
//				genes[i]=new BooleanGene(conf);
//			}
//			
//			IChromosome chromosome=new Chromosome(conf,genes);
//			return chromosome;
//		}catch(InvalidConfigurationException iex){
//			throw new IllegalStateException(iex.getMessage());
//		}
//	}
//	
//	public Configuration createConfiguration()
//	        throws InvalidConfigurationException {
//		 Configuration config = new Configuration();
//		 BestChromosomesSelector bestChromsSelector =new BestChromosomesSelector(config, 1.0d);
//		 bestChromsSelector.setDoubletteChromosomesAllowed(true);
//		 config.addNaturalSelector(bestChromsSelector, true);
//		 config.setRandomGenerator(new StockRandomGenerator());
//		 config.setEventManager(new EventManager());
//		 config.setFitnessEvaluator(new DefaultFitnessEvaluator());
//		 config.setChromosomePool(new ChromosomePool());
//		 config.addGeneticOperator(new GreedyCrossover(config));
//		 config.addGeneticOperator(new SwappingMutationOperator(config, 20));
//		 return config;
//		 
//	}
//	
//	
//}
