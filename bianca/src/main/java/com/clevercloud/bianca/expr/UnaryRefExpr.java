/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
 * Copyright (c) 2011-2012 Clever Cloud SAS -- all rights reserved
 *
 * This file is part of Resin(R) Open Source
 *
 * Each copy or derived work must preserve the copyright notice and this
 * notice unmodified.
 *
 * Resin Open Source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Resin Open Source is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, or any warranty
 * of NON-INFRINGEMENT.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resin Open Source; if not, write to the
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
import com.clevercloud.bianca.parser.BiancaParser;

/**
 * Represents a PHP reference argument.
 */
public class UnaryRefExpr extends AbstractUnaryExpr {

   public UnaryRefExpr(Location location, Expr expr) {
      super(location, expr);
   }

   public UnaryRefExpr(Expr expr) {
      super(expr);
   }

   /**
    * Returns true for a reference.
    */
   @Override
   public boolean isRef() {
      return true;
   }

   /**
    * Creates an assignment using this value as the right hand side.
    */
   @Override
   public Expr createAssignFrom(BiancaParser parser,
           AbstractVarExpr leftHandSide) {
      ExprFactory factory = parser.getExprFactory();

      return factory.createAssignRef(leftHandSide, _expr);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    *
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      // bianca/0d28
      Value value = getExpr().evalVar(env);

      return value.toRef();
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    *
    * @return the expression value.
    */
   @Override
   public Value evalArg(Env env, boolean isTop) {
      Value value = getExpr().evalVar(env);

      return value.toArgRef();
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    *
    * @return the expression value.
    */
   @Override
   public Var evalVar(Env env) {
      Var value = getExpr().evalVar(env);

      // php/112d
      return value;
      /*
      if (value instanceof Var)
      return new RefVar((Var) value);
      else
      return value;
       */
   }

   @Override
   public String toString() {
      return _expr.toString();
   }
}
