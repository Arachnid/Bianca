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

import com.clevercloud.vfs.WriteStream;

import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * Represents a call to an object's method
 */
public class CallbackObjectMethod extends Callback {

   private final Value _obj;
   private final StringValue _methodName;
   private final int _hash;

   public CallbackObjectMethod(Env env,
                               Value obj,
                               StringValue methodName) {
      // TODO: obj and fun should not be mixed

      _methodName = methodName;
      _obj = obj;

      _hash = methodName.hashCodeCaseInsensitive();
   }

   /**
    * Evaluates the callback with no arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env) {
      return _obj.callMethod(env, _methodName, _hash);
   }

   /**
    * Evaluates the callback with 1 argument.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1) {
      return _obj.callMethod(env, _methodName, _hash,
         a1);
   }

   /**
    * Evaluates the callback with 2 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2) {
      return _obj.callMethod(env, _methodName, _hash,
         a1, a2);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3) {
      return _obj.callMethod(env, _methodName, _hash,
         a1, a2, a3);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3,
                     Value a4) {
      return _obj.callMethod(env, _methodName, _hash,
         a1, a2, a3, a4);
   }

   /**
    * Evaluates the callback with 3 arguments.
    *
    * @param env the calling environment
    */
   @Override
   public Value call(Env env, Value a1, Value a2, Value a3,
                     Value a4, Value a5) {
      return _obj.callMethod(env, _methodName, _hash,
         a1, a2, a3, a4, a5);
   }

   @Override
   public Value call(Env env, Value[] args) {
      return _obj.callMethod(env, _methodName, _hash, args);
   }

   @Override
   public void varDumpImpl(Env env,
                           WriteStream out,
                           int depth,
                           IdentityHashMap<Value, String> valueSet)
      throws IOException {
      out.print(getClass().getName());
      out.print('[');
      out.print(_methodName);
      out.print(']');
   }

   @Override
   public boolean isValid(Env env) {
      return true;
   }

   @Override
   public String getCallbackName() {
      return _methodName.toString();
   }

   @Override
   public boolean isInternal(Env env) {
      // return _fun instanceof JavaInvoker;
      return false;
   }
}
