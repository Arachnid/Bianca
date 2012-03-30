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
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.env.Var;
import com.clevercloud.util.L10N;

/**
 * Represents the character at expression
 */
public class BinaryCharAtExpr extends AbstractVarExpr {

   private static final L10N L = new L10N(BinaryCharAtExpr.class);
   protected final Expr _objExpr;
   protected final Expr _indexExpr;

   public BinaryCharAtExpr(Location location, Expr objExpr, Expr indexExpr) {
      super(location);
      _objExpr = objExpr;
      _indexExpr = indexExpr;
   }

   public BinaryCharAtExpr(Expr objExpr, Expr indexExpr) {
      _objExpr = objExpr;
      _indexExpr = indexExpr;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      Value obj = _objExpr.eval(env);

      return obj.charValueAt(_indexExpr.evalLong(env));
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Var evalVar(Env env) {
      return eval(env).toVar();
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalArg(Env env, boolean isTop) {
      return eval(env);
   }

   /**
    * Evaluates the expression as an assignment.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalAssignRef(Env env, Value value) {
      Value obj = _objExpr.eval(env);

      Value result = obj.setCharValueAt(_indexExpr.evalLong(env),
         value);

      _objExpr.evalAssignValue(env, result);

      return value;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public void evalUnset(Env env) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return _objExpr + "{" + _indexExpr + "}";
   }
}
