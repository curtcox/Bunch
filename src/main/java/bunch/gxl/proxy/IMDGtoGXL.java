package bunch.gxl.proxy;

public interface IMDGtoGXL {
     boolean convert();
     void setOptions(String mdgF, String gxlF);
     void setOptions(String mdgF, String gxlF, boolean embed);
     void setOptions(String mdgF, String gxlF, String gxlPath);
     void setOptions(String mdgF, String gxlF, String gxlPath, boolean embed);
}