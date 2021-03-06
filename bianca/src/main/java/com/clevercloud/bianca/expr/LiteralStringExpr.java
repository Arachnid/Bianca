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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.expr;

import com.clevercloud.bianca.Location;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.parser.BiancaParser;

/**
 * Represents a PHP string literal expression.
 */
public class LiteralStringExpr extends Expr {

   protected final StringValue _value;

   public LiteralStringExpr(Location location, String value) {
      super(location);

      _value = new StringValue(value);
   }

   public LiteralStringExpr(Location location, StringValue value) {
      super(location);

      _value = value;
   }

   public LiteralStringExpr(String value) {
      this(Location.UNKNOWN, value);
   }

   public LiteralStringExpr(StringValue value) {
      this(Location.UNKNOWN, value);
   }

   /**
    * Returns true for a literal expression.
    */
   @Override
   public boolean isLiteral() {
      return true;
   }

   /**
    * Returns true if the expression evaluates to a string.
    */
   @Override
   public boolean isString() {
      return true;
   }

   //
   // expression creation
   //

   /**
    * Creates a class field $class::foo
    */
   @Override
   public Expr createClassConst(BiancaParser parser, String name) {
      ExprFactory factory = parser.getExprFactory();

      String className = _value.toString();

      if ("self".equals(className)) {
         className = parser.getSelfClassName();

         return factory.createClassConst(className, name);
      } else if ("parent".equals(className)) {
         className = parser.getParentClassName();

         return factory.createClassConst(className, name);
      } else if ("static".equals(className)) {
         return factory.createClassVirtualConst(name);
      } else {
         return factory.createClassConst(className, name);
      }
   }

   /**
    * Evaluates the expression as a constant.
    *
    * @return the expression value.
    */
   @Override
   public Value evalConstant() {
      return _value;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      return _value;
   }

   /**
    * Evaluates the expression as a string value.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public StringValue evalStringValue(Env env) {
      return _value;
   }

   @Override
   public String toString() {
      return "\"" + _value + "\"";
   }
}
