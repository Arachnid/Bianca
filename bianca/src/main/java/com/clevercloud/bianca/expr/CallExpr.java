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
import com.clevercloud.bianca.env.BiancaClass;
import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.NullValue;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.function.AbstractFunction;
import com.clevercloud.util.L10N;

import java.util.ArrayList;

/**
 * A "foo(...)" function call.
 */
public class CallExpr extends Expr {

   private static final L10N L = new L10N(CallExpr.class);
   protected final String _name;
   protected final String _nsName;
   protected final Expr[] _args;
   private int _funId;
   protected boolean _isRef;

   public CallExpr(Location location, String name, ArrayList<Expr> args) {
      // bianca/120o
      super(location);
      _name = name.intern();

      int ns = _name.lastIndexOf('\\');

      if (ns > 0) {
         _nsName = _name.substring(ns + 1);
      } else {
         _nsName = null;
      }

      _args = new Expr[args.size()];
      args.toArray(_args);
   }

   public CallExpr(Location location, String name, Expr[] args) {
      // bianca/120o
      super(location);
      _name = name.intern();

      int ns = _name.lastIndexOf('\\');

      if (ns > 0) {
         _nsName = _name.substring(ns + 1);
      } else {
         _nsName = null;
      }

      _args = args;
   }

   public CallExpr(String name, ArrayList<Expr> args) {
      this(Location.UNKNOWN, name, args);
   }

   public CallExpr(String name, Expr[] args) {
      this(Location.UNKNOWN, name, args);
   }

   /**
    * Returns the name.
    */
   public String getName() {
      return _name;
   }

   /**
    * Returns the location if known.
    */
   @Override
   public String getFunctionLocation() {
      return " [" + _name + "]";
   }

   /**
    * Returns the reference of the value.
    * @param location
    */
   /*
   @Override
   public Expr createRef(BiancaParser parser)
   {
   return parser.getExprFactory().createCallRef(this);
   }
    */

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
      return evalImpl(env, false, false);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalCopy(Env env) {
      return evalImpl(env, false, true);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value evalRef(Env env) {
      return evalImpl(env, true, true);
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   private Value evalImpl(Env env, boolean isRef, boolean isCopy) {
      if (_funId <= 0) {
         _funId = env.findFunctionId(_name);

         if (_funId <= 0) {
            if (_nsName != null) {
               _funId = env.findFunctionId(_nsName);
            }

            if (_funId <= 0) {
               env.error(getLocationLine(),
                  L.l("'{0}' is an unknown function.", _name));

               return NullValue.NULL;
            }
         }
      }

      AbstractFunction fun = env.findFunction(_funId);

      if (fun == null) {
         env.error(getLocationLine(), L.l("'{0}' is an unknown function.", _name));

         return NullValue.NULL;
      }

      Value[] args = evalArgs(env, _args);

      env.pushCall(this, NullValue.NULL, args);

      // php/0249
      BiancaClass oldCallingClass = env.setCallingClass(null);

      // TODO: qa/1d14 Value oldThis = env.setThis(UnsetValue.NULL);
      try {
         /*
         if (isRef)
         return fun.callRef(env, args);
         else if (isCopy)
         return fun.callCopy(env, args);
         else
         return fun.call(env, args);
          */

         if (isRef) {
            return fun.callRef(env, args);
         } else if (isCopy) {
            return fun.call(env, args).copyReturn();
         } else {
            return fun.call(env, args).toValue();
         }
         //} catch (Exception e) {
         //  throw BiancaException.create(e, env.getStackTrace());
      } finally {
         env.popCall();
         env.setCallingClass(oldCallingClass);
         // TODO: qa/1d14 env.setThis(oldThis);
      }
   }

   // Return an array containing the Values to be
   // passed in to this function.
   public Value[] evalArguments(Env env) {
      AbstractFunction fun = env.findFunction(_name);

      if (fun == null) {
         return null;
      }

      return fun.evalArguments(env, this, _args);
   }

   @Override
   public String toString() {
      return _name + "()";
   }
}
