package bunch.simple;

import bunch.model.GenericFactory;

public class SATechniqueFactory extends GenericFactory {

  String  defaultFactoryItem = "Simple Algorithm";

  public SATechniqueFactory() {
      super();
      setFactoryType("SATechnique");
      addItem("Simple Algorithm", "bunch.simple.SASimpleTechnique");
  }

  public String getDefaultTechnique()
  {
    return defaultFactoryItem;
  }

public SATechnique getMethod(String name)
{
  return (SATechnique)getItemInstance(name);
}
}