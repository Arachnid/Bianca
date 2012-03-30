/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Bianca(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Bianca Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Bianca Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Bianca Open Source; if not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330
 *   Boston, MA 02111-1307  USA
 *
 * @author Scott Ferguson
 */
package com.clevercloud.bianca.expr;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.parser.BiancaParser;

/**
 * Represents a PHP constant expression.
 */
public class ConstExpr extends Expr {

   protected final String _var;

   public ConstExpr(Location location, String var) {
      super(location);
      _var = var;
   }

   public ConstExpr(String var) {
      this(Location.UNKNOWN, var);// acceptable, for compiled code
   }

   /**
    * Returns the variable.
    */
   public String getVar() {
      return _var;
   }

   //
   // expression creation
   //

   /**
    * Creates a class field Foo::bar
    */
   @Override
   public Expr createClassConst(BiancaParser parser, String name) {
      ExprFactory factory = parser.getExprFactory();

      String className = _var;
      String specialClassName = getSpecialClassName();

      if ("self".equals(specialClassName)) {
         className = parser.getSelfClassName();

         return factory.createClassConst(className, name);
      } else if ("parent".equals(specialClassName)) {
         className = parser.getParentClassName();

         return factory.createClassConst(className, name);
      } else if ("static".equals(specialClassName)) {
         return factory.createClassVirtualConst(name);
      } else {
         return factory.createClassConst(className, name);
      }
   }

   /**
    * Creates a class field Foo::$bar
    */
   @Override
   public Expr createClassField(BiancaParser parser, String name) {
      ExprFactory factory = parser.getExprFactory();

      String className = _var;
      String specialClassName = getSpecialClassName();

      if ("self".equals(specialClassName)) {
         className = parser.getSelfClassName();

         return factory.createClassField(className, name);
      } else if ("parent".equals(specialClassName)) {
         className = parser.getParentClassName();

         return factory.createClassField(className, name);
      } else if ("static".equals(specialClassName)) {
         return factory.createClassVirtualField(name);
      } else {
         return factory.createClassField(className, name);
      }
   }

   /**
    * Creates a class field Foo::${bar}
    */
   @Override
   public Expr createClassField(BiancaParser parser, Expr name) {
      ExprFactory factory = parser.getExprFactory();

      String className = _var;
      String specialClassName = getSpecialClassName();

      if ("self".equals(specialClassName)) {
         className = parser.getSelfClassName();

         return factory.createClassField(className, name);
      } else if ("parent".equals(specialClassName)) {
         className = parser.getParentClassName();

         return factory.createClassField(className, name);
      } else if ("static".equals(specialClassName)) {
         return factory.createClassVirtualField(name);
      } else {
         return factory.createClassField(className, name);
      }
   }

   private String getSpecialClassName() {
      String className = _var;

      int ns = className.lastIndexOf('\\');

      if (ns >= 0) {
         return className.substring(ns + 1);
      } else {
         return className;
      }
   }

   /**
    * Returns true for literal
    */
   @Override
   public Value evalConstant() {
      return new StringValue(_var);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      return env.getConstant(_var);
   }

   @Override
   public String toString() {
      return _var;
   }
}
