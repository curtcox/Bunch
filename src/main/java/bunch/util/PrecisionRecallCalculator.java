package bunch.util;

import java.util.*;
import java.io.*;

public final class PrecisionRecallCalculator {

  private final String m_S_filename1;
  private final String m_S_filename2;

  private Double S_precision;
  private Double S_recall;
  private Vector<Vector> m_v_expert_modules_content = new Vector<>();
  private Vector<String> m_v_tested_modules_names = new Vector<>();
  private Vector<Vector> m_v_tested_modules_content = new Vector<>();

  public PrecisionRecallCalculator(String expertFileName, String testFileName) {
    m_S_filename1= expertFileName;
    m_S_filename2= testFileName;
    ReadBunch();
    compare();
  }

  public Double get_precision() {
    return S_precision;
  }

  public Double get_recall() {
    return S_recall;
  }

  private void compare() {
    Compare cp = new Compare(m_v_expert_modules_content, m_v_tested_modules_content, m_v_tested_modules_names);
    cp.do_compare();
    S_precision = cp.get_precision();
    S_recall = cp.get_recall();

  }

  private void ReadBunch() {
    GBunchRW bunch1= new GBunchRW(m_S_filename1);

    bunch1.read();

    Vector<String> m_v_expert_modules_names = bunch1.getModuleNames();
    m_v_expert_modules_content = bunch1.getModulesContent();

    //remove all the tree information
    boolean found;
    for (int i = 0; i< m_v_expert_modules_names.size(); i++) {
      Vector v_module_content = new Vector(m_v_expert_modules_content.get(i));
      found = false;
      for (int j=0;j<v_module_content.size() && !found;j++) {
        if (m_v_expert_modules_names.contains(v_module_content.get(j))) {
          m_v_expert_modules_names.remove(i);
          m_v_expert_modules_content.remove(i);
          i--;
          found = true;
        }
      }
    }

    //read the tested clusters
    GBunchRW bunch2= new GBunchRW(m_S_filename2);

    bunch2.read();

    m_v_tested_modules_names = bunch2.getModuleNames();
    m_v_tested_modules_content = bunch2.getModulesContent();

    //remove all the tree information
    for (int i =0; i<m_v_tested_modules_names.size();i++) {
      String S_module_name = m_v_tested_modules_names.get(i);
      Vector v_module_content = new Vector(m_v_tested_modules_content.get(i));
      found = false;
      for (int j=0;j<v_module_content.size() && !found;j++)
      {
        if (m_v_tested_modules_names.contains(v_module_content.get(j)))
        {
          m_v_tested_modules_names.remove(i);
          m_v_tested_modules_content.remove(i);
          i--;
          found = true;
        }
      }
    }

  }

}


final class Compare {
  private final Vector m_v_original_distance;
  private final Vector m_v_new_distance_name;
  private double m_d_recall, m_d_precision;

  public double get_precision() {
    return m_d_precision;
  }

  public double get_recall() {
    return m_d_recall;
  }

  public Compare(Vector orig, Vector newname, Vector newnumber) {
    // used to get the index of the original vars
    Hashtable m_ht_vars_orig = new Hashtable();
    // used to get the index of the new vars
    Hashtable m_ht_vars_new = new Hashtable();
    m_v_original_distance = new Vector<>(orig);
    m_v_new_distance_name = new Vector<>(newname);
    m_d_recall =0.0;
    m_d_precision=0.0;
  }

  public void do_compare() {
    boolean found1;
    int pairs_found = 0 ;
    int pairs_total = 0;

    Vector v_temp;
    Vector v_new = new Vector();
    for (Object value : m_v_original_distance) {
      v_temp = (Vector) value;
      //System.out.println(v_temp.size());
      pairs_total += v_temp.size() * (v_temp.size() - 1) / 2;
      //System.out.println("Total number of pairs: "+pairs_total);

      for (int j = 0; j < v_temp.size() - 1; j++) {
        found1 = false;
        String s_var1 = v_temp.get(j).toString();
        for (int l = 0; l < m_v_new_distance_name.size() && !found1; l++) //this will find the first variable in the new clusters
        {
          v_new = (Vector) m_v_new_distance_name.get(l);
          if (v_new.indexOf(s_var1) >= 0) {
            //System.out.println("______________________________FOUND: "+s_var1+" __________________\n"+v_new);
            found1 = true;
          }
        }
        //System.out.println("Total: "+pairs_total);
        if (!found1) {
          pairs_total -= (v_temp.size() - 1 - j);
          //System.out.println("Total after not found: "+pairs_total+" removed: "+(v_temp.size()-1-j));
        }
        if (found1) {
          //System.out.println("Latest New: "+v_new);
          //System.out.println("v1: "+s_var1);
          for (int k = j + 1; k < v_temp.size(); k++) {
            String s_var2 = v_temp.get(k).toString();
            //System.out.println("v1: "+s_var1+" - v2: "+s_var2);
            if (v_new.indexOf(s_var2) >= 0) {
              pairs_found++;
              //System.out.println("Found a pair: "+s_var1+" "+s_var2);
            }
          }
          //System.out.println("Found: "+pairs_found+" pairs");
        }
      }
    }
    if (pairs_total!=0)
      m_d_recall = (double)pairs_found*100/pairs_total;
    //System.out.println("Recall= "+m_d_recall+"% not found: "+not_found);

// this part will calculate the precision

    pairs_found = 0 ;
    pairs_total = 0;

    for (Object o : m_v_new_distance_name) {
      v_temp = (Vector) o;
      //System.out.println(v_temp.size());
      pairs_total += v_temp.size() * (v_temp.size() - 1) / 2;
      //System.out.println("Total number of pairs: "+pairs_total);

      for (int j = 0; j < v_temp.size() - 1; j++) {
        found1 = false;
        String s_var1 = v_temp.get(j).toString();
        for (int l = 0; l < m_v_original_distance.size() && !found1; l++) //this will find the first variable in the new clusters
        {
          v_new = (Vector) m_v_original_distance.get(l);
          if (v_new.indexOf(s_var1) >= 0) {
            //System.out.println("______________________________FOUND: "+s_var1+" __________________\n"+v_new);
            found1 = true;
          }
        }
        //System.out.println("Total: "+pairs_total);
        if (!found1) {
          pairs_total -= (v_temp.size() - 1 - j);
          //System.out.println("Total after not found: "+pairs_total+" removed: "+(v_temp.size()-1-j));
        }
        if (found1) {
          //System.out.println("Latest New: "+v_new);
          //System.out.println("v1: "+s_var1);
          for (int k = j + 1; k < v_temp.size(); k++) {
            String s_var2 = v_temp.get(k).toString();
            //System.out.println("v1: "+s_var1+" - v2: "+s_var2);
            if (v_new.indexOf(s_var2) >= 0) {
              pairs_found++;
              //System.out.println("Found a pair: "+s_var1+" "+s_var2);
            }
          }
          //System.out.println("Found: "+pairs_found+" pairs");
        }
      }
    }
    if (pairs_total!=0)
      m_d_precision=(double)pairs_found*100/pairs_total;
    //System.out.println("Precision= "+m_d_precision+"% not found: "+not_found);
  }

}

final class GBunchRW {
  private final Hashtable m_ht_bunchread;
  private final String m_S_filename;

  public GBunchRW(String filename) {
    m_ht_bunchread = new Hashtable(); //the main hashtable that will be returned
    m_S_filename = filename;
  }

  public void read()
  {
    int i_start_location_of_SS =0;
    int i_end_location_of_SS =0;
    String S_module_name = "";
    Vector v_module_value = new Vector();

    try {
      BufferedReader br = new BufferedReader(new FileReader(m_S_filename));
      while (true) {
        v_module_value = new Vector();
        S_module_name = "";

        String line = br.readLine();

        if (line == null)
      	  break;
        line = line.trim();

        if (line.length() == 0)
          continue;

        if (line.length() > 1 && line.charAt(0) == '/' && line.trim().charAt(1) == '/')
          continue;

        i_start_location_of_SS = line.indexOf("SS(")+3;
        i_end_location_of_SS = line.indexOf(")");
        S_module_name = line.substring(i_start_location_of_SS, i_end_location_of_SS);
        line = line.substring(line.indexOf("=")+1);

        //System.out.println(line+" :"+S_module_name+".");

        StringTokenizer st = new StringTokenizer(line, ",");
        while (st.hasMoreTokens())
          v_module_value.add(st.nextToken().trim());

        m_ht_bunchread.put(S_module_name,v_module_value);
      }

    } catch (java.io.IOException e) {
      System.out.println("Opps: "+e);
    }

    m_ht_bunchread.clone();
  }

  public void write(Hashtable ht) {
    try {
      FileWriter fos = new FileWriter(m_S_filename);

      fos.write("//Created automatically using GBunchRW...\n");
    Enumeration keys = ht.keys();
    while (keys.hasMoreElements()) {
      String S_temp = keys.nextElement().toString();
      Vector v_temp = new Vector((Vector)ht.get(S_temp));

      fos.write("SS("+S_temp+")= ");  //write the name of every module
      for (int i=0;i<v_temp.size()-1;i++)
      {
        fos.write(v_temp.get(i)+", ");
      }
      fos.write(v_temp.get(v_temp.size()-1).toString()+"\n"); //write the last var
    }

      fos.close();
    } catch (java.io.IOException e) {
      System.out.println("Opps: "+e);
    }

  }//end of method

  public Vector getModuleNames() {
    Vector v_temp = new Vector();
    Enumeration keys = m_ht_bunchread.keys();
    while (keys.hasMoreElements()) {
      String S_temp = keys.nextElement().toString();
      v_temp.add(S_temp);
    }

    return (Vector)v_temp.clone();
  }

  public Vector getModulesContent() {
    Vector v_modules = new Vector();

    Enumeration keys = m_ht_bunchread.keys();
    while (keys.hasMoreElements()) {
      String S_temp = keys.nextElement().toString();
      Vector v_temp = new Vector((Vector)m_ht_bunchread.get(S_temp));
      v_modules.add(v_temp);
    }
    return (Vector)v_modules.clone();
  }
} //end of class
