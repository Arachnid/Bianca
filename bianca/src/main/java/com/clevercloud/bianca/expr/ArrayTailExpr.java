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
import com.clevercloud.bianca.env.*;

/**
 * Represents a PHP array[] reference expression.
 */
public class ArrayTailExpr extends AbstractVarExpr {

   protected final Expr _expr;

   public ArrayTailExpr(Location location, Expr expr) {
      super(location);
      _expr = expr;
   }

   public ArrayTailExpr(Expr expr) {
      _expr = expr;
   }

   /**
    * Returns true for an expression that can be read (only $a[] uses this)
    */
   @Override
   public boolean canRead() {
      return false;
   }

   /**
    * Returns the expr.
    */
   public Expr getExpr() {
      return _expr;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      return env.error(getLocation(), "Cannot use [] as a read-value.");
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalArg(Env env, boolean isTop) {
      return evalVar(env);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Var evalVar(Env env) {
      Value obj = _expr.evalVar(env);

      return obj.putVar();
   }

   /**
    * Evaluates the expression, setting an array if unset..
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalArray(Env env) {
      Value obj = _expr.evalArray(env);

      ArrayValue array = new ArrayValueImpl();

      obj.put(array);

      return array;
   }

   /**
    * Evaluates the expression, assigning an object if unset..
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalObject(Env env) {
      Value array = _expr.evalArray(env);

      Value value = env.createObject();

      array.put(value);

      return value;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalAssignValue(Env env, Value value) {
      Value array = _expr.evalVar(env);

      array = array.toAutoArray();

      array.put(value);

      return value;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalAssignRef(Env env, Value value) {
      Value array = _expr.evalArray(env);

      array.put(value);

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
      return _expr + "[]";
   }
}
