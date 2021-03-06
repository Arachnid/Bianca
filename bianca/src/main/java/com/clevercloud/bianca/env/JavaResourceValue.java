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
 * @author Nam Nguyen
 */
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.program.JavaClassDef;

import java.io.Serializable;

/**
 * Represents a Bianca java value representing a PHP resource value.
 */
public class JavaResourceValue extends JavaValue
   implements Serializable {

   public JavaResourceValue(Env env, Object object, JavaClassDef def) {
      super(env, object, def);
   }

   /**
    * Returns true for an object.
    */
   @Override
   public boolean isObject() {
      return false;
   }

   /*
    * Returns true for a resource.
    */
   @Override
   public boolean isResource() {
      return true;
   }

   /**
    * Returns the type.
    */
   @Override
   public String getType() {
      return "resource";
   }

   /*
    * Returns the resource type.
    */
   @Override
   public String getResourceType() {
      return getJavaClassDef().getResourceType();
   }
}
