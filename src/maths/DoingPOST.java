/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2009 Toporov Konstantin. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 3 only ("GPL")  (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.gnu.org/copyleft/gpl.html  See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * You should include file containing license in each project.
 * If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by
 * the GPL Version 3, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [GPL Version 3] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under the GPL Version 3 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 3 code and therefore, elected the GPL
 * Version 3 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009-2017 Toporov Konstantin.
 */

package maths;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */


public class DoingPOST {
    private final static String SCRIPT = "srv.php";
    public final static String SERVER = "http://diacalc.org/dbwork/";
    private String answer = "";
    private String errorMsg = "";
    private boolean error = false;
    
    public static String checkServerPath(String v){
        if (v==null || v.length()==0) return SERVER;
        if (!v.endsWith("/")) return v+"/";
        return v;
    }
    
    public DoingPOST(String pathToServer, String request){
        URL                 url;
        URLConnection       urlConn;
        DataOutputStream    printout;
        
        try{
            url = new URL (checkServerPath(pathToServer) + SCRIPT);
            urlConn = url.openConnection();
            urlConn.setDoInput (true);
            urlConn.setDoOutput (true);
            urlConn.setUseCaches (false);

            // Specify the content type.
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            printout = new DataOutputStream (urlConn.getOutputStream ());

            printout.writeBytes (request);
            printout.flush ();
            printout.close ();

            // Get response data.
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF8"));

            String line;
            while (null != ((line = rd.readLine())))
            {
                answer += line;
            }
            rd.close ();
            if (answer.startsWith("error")){
                error = true;
                errorMsg = answer.replace("<br>", "\n");
            }
        }
        catch(Exception ex){
            errorMsg += "Exception while connecting to the server:"+ex.getMessage();
            error = true;
        }
        
        //answer = answer.replace("<br>", "\n");
    }
    
    public String getAnswer(){
        return answer;
    }
    
    public boolean isError(){
        return error;
    }
    
    public String getErrorMessage(){
        return errorMsg;
    }
    
    public static String addKeyValue(String key, String value){
        return addKeyValue( null, key, value );
    }
    
    public static String addKeyValue(String query, String key, String value){
        String res;
        if (query==null){
            res = "";
        }
        else res = query;
        if (res.length()>0){
            res += "&";
        }
        try{
            res += key + "=" + URLEncoder.encode (value,"UTF8");
        }
        catch (UnsupportedEncodingException ex){
            res += key + "=" + value;
        }
        return res;
    }
}
