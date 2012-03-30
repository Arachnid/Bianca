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
import com.clevercloud.bianca.env.NullValue;
import com.clevercloud.bianca.env.StringValue;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.vfs.Path;

/**
 * Represents a PHP include statement
 */
public class FunIncludeExpr extends AbstractUnaryExpr {

   protected Path _dir;
   protected boolean _isRequire;

   public FunIncludeExpr(Location location, Path sourceFile, Expr expr) {
      super(location, expr);

      _dir = sourceFile.getParent();
   }

   public FunIncludeExpr(Location location,
                         Path sourceFile,
                         Expr expr,
                         boolean isRequire) {
      this(location, sourceFile, expr);

      _isRequire = isRequire;
   }

   public FunIncludeExpr(Path sourceFile, Expr expr) {
      super(expr);

      _dir = sourceFile.getParent();
   }

   public FunIncludeExpr(Path sourceFile, Expr expr, boolean isRequire) {
      this(sourceFile, expr);

      _isRequire = isRequire;
   }

   /**
    * Evaluates the expression.
    *
    * @param env the calling environment.
    * @return the expression value.
    */
   @Override
   public Value eval(Env env) {
      StringValue name = _expr.eval(env).toStringValue();

      env.pushCall(this, NullValue.NULL, new Value[]{name});
      try {
         return env.include(_dir, name, _isRequire, false);
      } finally {
         env.popCall();
      }
   }

   @Override
   public String toString() {
      return _expr.toString();
   }
}
