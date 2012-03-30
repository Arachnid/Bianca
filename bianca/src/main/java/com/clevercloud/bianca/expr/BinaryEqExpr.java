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
import com.clevercloud.bianca.env.BooleanValue;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;

/**
 * Represents a PHP equality testing expression.
 */
public class BinaryEqExpr extends AbstractBinaryExpr {

   public BinaryEqExpr(Location location, Expr left, Expr right) {
      super(location, left, right);
   }

   public BinaryEqExpr(Expr left, Expr right) {
      super(left, right);
   }

   @Override
   public boolean isBoolean() {
      return true;
   }

   /**
    * Evaluates the equality as a boolean.
    */
   @Override
   public Value eval(Env env) {
      return evalBoolean(env) ? BooleanValue.TRUE : BooleanValue.FALSE;
   }

   /**
    * Evaluates the equality as a boolean.
    */
   @Override
   public boolean evalBoolean(Env env) {
      Value lValue = _left.eval(env);
      Value rValue = _right.eval(env);

      return lValue.eq(rValue);
   }

   @Override
   public String toString() {
      return "(" + _left + " == " + _right + ")";
   }
}
