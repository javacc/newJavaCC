/* Copyright (c) 2006, Sun Microsystems, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Sun Microsystems, Inc. nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.javacc.parser;

/**
 * Generates the Constants file.
 */
public class OtherFilesGen extends JavaCCGlobals implements JavaCCParserConstants {

  public static boolean keepLineCol;
  static public void start() throws MetaParseException {

    Token t = null;
    keepLineCol = Options.getKeepLineColumn();

    if (JavaCCErrors.get_error_count() != 0) throw new MetaParseException();

    JavaFiles.gen_TokenMgrError();
    JavaFiles.gen_ParseException();
    JavaFiles.gen_Token();
    if (Options.getUserTokenManager()) {
      JavaFiles.gen_TokenManager();
    } else if (Options.getUserCharStream()) {
      JavaFiles.gen_CharStream();
    } else {
      if (Options.getJavaUnicodeEscape()) {
        JavaFiles.gen_JavaCharStream();
      } else {
        JavaFiles.gen_SimpleCharStream();
      }
    }

    try {
      ostr = new java.io.PrintWriter(
                new java.io.BufferedWriter(
                   new java.io.FileWriter(
                     new java.io.File(Options.getOutputDirectory(), cu_name + "Constants.java")
                   ),
                   8192
                )
             );
    } catch (java.io.IOException e) {
      JavaCCErrors.semantic_error("Could not open file " + cu_name + "Constants.java for writing.");
      throw new Error();
    }

    java.util.Vector tn = (java.util.Vector)(toolNames.clone());
    tn.addElement(toolName);
    ostr.println("/* " + getIdString(tn, cu_name + "Constants.java") + " */");

    if (cu_to_insertion_point_1.size() != 0 &&
        ((Token)cu_to_insertion_point_1.elementAt(0)).kind == PACKAGE
       ) {
      for (int i = 1; i < cu_to_insertion_point_1.size(); i++) {
        if (((Token)cu_to_insertion_point_1.elementAt(i)).kind == SEMICOLON) {
          printTokenSetup((Token)(cu_to_insertion_point_1.elementAt(0)));
          for (int j = 0; j <= i; j++) {
            t = (Token)(cu_to_insertion_point_1.elementAt(j));
            printToken(t, ostr);
          }
          printTrailingComments(t, ostr);
          ostr.println("");
          ostr.println("");
          break;
        }
      }
    }
    ostr.println("public interface " + cu_name + "Constants {");
    ostr.println("");
    RegularExpression re;
    ostr.println("  int EOF = 0;");
    for (java.util.Enumeration enumeration = ordered_named_tokens.elements(); enumeration.hasMoreElements();) {
      re = (RegularExpression)enumeration.nextElement();
      ostr.println("  int " + re.label + " = " + re.ordinal + ";");
    }
    ostr.println("");
    if (!Options.getUserTokenManager() && Options.getBuildTokenManager()) {
      for (int i = 0; i < LexGen.lexStateName.length; i++) {
        ostr.println("  int " + LexGen.lexStateName[i] + " = " + i + ";");
      }
      ostr.println("");
    }
    ostr.println("  String[] tokenImage = {");
    ostr.println("    \"<EOF>\",");

    for (java.util.Enumeration enumeration = rexprlist.elements(); enumeration.hasMoreElements();) {
      TokenProduction tp = (TokenProduction)(enumeration.nextElement());
      java.util.Vector respecs = tp.respecs;
      for (java.util.Enumeration enum1 = respecs.elements(); enum1.hasMoreElements();) {
        RegExprSpec res = (RegExprSpec)(enum1.nextElement());
        re = res.rexp;
        if (re instanceof RStringLiteral) {
          ostr.println("    \"\\\"" + add_escapes(add_escapes(((RStringLiteral)re).image)) + "\\\"\",");
        } else if (!re.label.equals("")) {
          ostr.println("    \"<" + re.label + ">\",");
        } else {
          if (re.tpContext.kind == TokenProduction.TOKEN) {
            JavaCCErrors.warning(re, "Consider giving this non-string token a label for better error reporting.");
          }
          ostr.println("    \"<token of kind " + re.ordinal + ">\",");
        }

      }
    }
    ostr.println("  };");
    ostr.println("");
    ostr.println("}");

    ostr.close();

  }

  static private java.io.PrintWriter ostr;

   public static void reInit()
   {
      ostr = null;
   }

}
