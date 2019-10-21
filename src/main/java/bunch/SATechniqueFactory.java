package bunch;

public class SATechniqueFactory extends GenericFactory{

  String  defaultFactoryItem = "Simple Algorithm";

  public SATechniqueFactory() {
      super();
      setFactoryType("SATechnique");
      addItem("Simple Algorithm", "bunch.SASimpleTechnique");
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