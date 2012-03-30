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
import com.clevercloud.bianca.env.Closure;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.parser.BiancaParser;
import com.clevercloud.bianca.program.Function;
import com.clevercloud.util.L10N;

/**
 * Represents a PHP closure expression.
 */
public class ClosureExpr extends Expr {

   private static final L10N L = new L10N(ClosureExpr.class);
   protected final Function _fun;

   public ClosureExpr(Location location, Function fun) {
      // bianca/120o
      super(location);

      _fun = fun;
   }

   /**
    * Returns the name.
    */
   public String getName() {
      return _fun.getName();
   }

   /**
    * Returns the function
    */
   public Function getFunction() {
      return _fun;
   }

   /**
    * Returns the location if known.
    */
   @Override
   public String getFunctionLocation() {
      return " [" + getName() + "]";
   }

   /**
    * Returns the reference of the value.
    *
    * @param location
    */
   @Override
   public Expr createRef(BiancaParser parser) {
      return parser.getFactory().createRef(this);
   }

   /**
    * Returns the copy of the value.
    *
    * @param location
    */
   @Override
   public Expr createCopy(ExprFactory factory) {
      return this;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      return evalImpl(env);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalCopy(Env env) {
      return evalImpl(env);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   private Value evalImpl(Env env) {
      return new Closure(env, _fun);
   }

   @Override
   public String toString() {
      return getName() + "()";
   }
}
