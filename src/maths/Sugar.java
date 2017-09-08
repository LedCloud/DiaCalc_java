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
 * Portions Copyrighted 2009 Toporov Konstantin.
 */

package maths;

/**
 *
 * @author Toporov Konstantin <www.diacalc.org>
 */

public class Sugar {
    public final static boolean MMOL = true;
    public final static boolean MGDL = false;
    public final static boolean PLASMA = true;
    public final static boolean WHOLE = false;

    private final static float GLUC = 18.015588f;
    private final static float PLASM = 1.12f;

    private float value = 5.6f;//храним в ммоль в цельной крови

    public Sugar(){
    }


    public Sugar(Sugar in_s){
        value = in_s.value;
    }

    public Sugar(float s){
        value = s;
    }

    public float getValue(){
        return value;
    }

    public void setSugar(float sugar,boolean mmol,boolean plasma){
        if (mmol) value = sugar;
        else value = sugar/GLUC;
        if (plasma) value = value / PLASM;
    }

    public float getSugar(boolean mmol,boolean plasma){
        float v;
        if (mmol) v =  value;
        else v = value*GLUC;
        if (plasma) return v*PLASM;
        return v;
    }
}
