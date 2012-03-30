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
import com.clevercloud.util.L10N;

import java.util.ArrayList;

/**
 * A "$foo->$bar(...)" method call
 */
public class ObjectMethodVarExpr extends Expr {

   private static final L10N L = new L10N(ObjectMethodVarExpr.class);
   protected final Expr _objExpr;
   protected final Expr _name;
   protected final Expr[] _args;
   protected Expr[] _fullArgs;

   public ObjectMethodVarExpr(Location location,
                              Expr objExpr,
                              Expr name,
                              ArrayList<Expr> args) {
      super(location);
      _objExpr = objExpr;

      _name = name;

      _args = new Expr[args.size()];
      args.toArray(_args);
   }

   public ObjectMethodVarExpr(Location location,
                              Expr objExpr,
                              Expr name,
                              Expr[] args) {
      super(location);
      _objExpr = objExpr;

      _name = name;

      _args = args;
   }

   public ObjectMethodVarExpr(Expr objExpr, Expr name, ArrayList<Expr> args) {
      this(Location.UNKNOWN, objExpr, name, args);
   }

   public ObjectMethodVarExpr(Expr objExpr, Expr name, Expr[] args) {
      this(Location.UNKNOWN, objExpr, name, args);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      Value[] values = new Value[_args.length];

      for (int i = 0; i < values.length; i++) {
         values[i] = _args[i].evalArg(env, true);
      }

      StringValue methodName = _name.eval(env).toStringValue();

      Value obj = _objExpr.eval(env);

      env.pushCall(this, obj, values);

      try {
         return obj.callMethod(env, methodName, values);
      } finally {
         env.popCall();
      }
   }

   @Override
   public String toString() {
      return _objExpr + "->" + _name + "()";
   }
}
