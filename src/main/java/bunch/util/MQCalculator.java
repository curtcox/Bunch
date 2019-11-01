package bunch.util;

import bunch.calculator.ObjectiveFunctionCalculator;
import bunch.calculator.ObjectiveFunctionCalculatorFactory;
import bunch.model.Graph;
import bunch.model.Node;
import bunch.parser.ClusterFileParser;
import bunch.parser.DependencyFileParser;
import bunch.parser.Parser;

public final class MQCalculator {

  public MQCalculator() { }

  public static double CalcMQ(String mdgFileName, String silFileName, ObjectiveFunctionCalculator calculatorName) {
    try {

        Parser p = new DependencyFileParser();
      p.setInput(mdgFileName);
      p.setDelims(" \t");

      Graph g = (Graph)p.parse();
      ObjectiveFunctionCalculatorFactory ofc = new ObjectiveFunctionCalculatorFactory();
      ofc.setCurrentCalculator(calculatorName);
      Graph.setObjectiveFunctionCalculatorFactory(ofc);

      g.setObjectiveFunctionCalculator(calculatorName);

      ClusterFileParser cfp = new ClusterFileParser();
      cfp.setInput(silFileName);
      cfp.setObject(g);
      cfp.parse();
      g.calculateObjectiveFunctionValue();

      //figure out the total number of edges
      long edgeCnt = 0;
      Node[] n = g.getNodes();
      for (Node node : n) {
        if (node.dependencies != null)
          edgeCnt += node.dependencies.length;
      }

      //set output values
      return g.getObjectiveFunctionValue();
      //System.out.println("Objective function value = " + g.getObjectiveFunctionValue());

    } catch(Exception calcExcept) {
      calcExcept.printStackTrace();
      return -1.0;
    }
  }
}