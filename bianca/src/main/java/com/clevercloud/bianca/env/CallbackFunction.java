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
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.function.AbstractFunction;

/**
 * Represents a call to a function.
 */
public class CallbackFunction extends Callback {
   // public static final CallbackFunction INVALID_CALLBACK
   // = new CallbackFunction(null, "Invalid Callback");

   private String _funName;
   private AbstractFunction _fun;

   // private boolean _isInvalid = false;
   public CallbackFunction(Env env, String funName) {
      _funName = funName;
   }

   public CallbackFunction(AbstractFunction fun) {
      _fun = fun;
   }

   public CallbackFunction(AbstractFunction fun, String funName) {
      _fun = fun;
      _funName = funName;
   }

   /**
    * Allow subclasses to set the abstract function directly.
    */
   protected void setFunction(AbstractFunction fun) {
      _fun = fun;
   }

   @Override
   public boolean isValid(Env env) {
      if (_fun != null) {
         return true;
      }

      return env.findFunction(_funName) != null;

      //return _isInvalid;
   }

   /**
    * Serializes the value.
    */
   @Override
   public void serialize(Env env, StringBuilder sb) {
      String name;

      if (_fun != null) {
         name = _fun.getName();
      } else {
         name = _funName;
      }

      sb.append("S:");
      sb.append(name.length());
      sb.append(":\"");
      sb.append(name);
      sb.append("\";");
   }

   /**
    * Evaluates the callback with no arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env) {
      return getFunction(env).call(env);
   }

   /**
    * Evaluates the callback with 1 argument.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1) {
      return getFunction(env).call(env, a1);
   }

   /**
    * Evaluates the callback with 2 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2) {
      return getFunction(env).call(env, a1, a2);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3) {
      return getFunction(env).call(env, a1, a2, a3);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3,
                     Value a4) {
      return getFunction(env).call(env, a1, a2, a3, a4);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3,
                     Value a4, Value a5) {
      return getFunction(env).call(env, a1, a2, a3, a4, a5);
   }

   @Override
   public Value call(Env env, Value[] args) {
      return getFunction(env).call(env, args);
   }

   @Override
   public String getCallbackName() {
      return _funName;
   }

   public AbstractFunction getFunction(Env env) {
      if (_fun == null) {
         _fun = env.getFunction(_funName);
      }

      return _fun;
   }

   @Override
   public boolean isInternal(Env env) {
      return getFunction(env) instanceof JavaInvoker;
   }

   /**
    * Exports the value.
    */
   @Override
   public void varExport(StringBuilder sb) {
      sb.append("'' . \"\\0\" . '").append(_funName.substring(1)).append("'");
   }

   @Override
   public String toString() {
      return getClass().getName() + '[' + _funName + ']';
   }
}
