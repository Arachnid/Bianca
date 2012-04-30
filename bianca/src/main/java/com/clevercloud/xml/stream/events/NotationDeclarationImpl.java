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
* @author Emil Ong
*/

package com.clevercloud.xml.stream.events;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import java.io.Writer;

public class NotationDeclarationImpl extends XMLEventImpl
   implements NotationDeclaration {
   private final String _name;
   private final String _publicId;
   private final String _systemId;

   public NotationDeclarationImpl(String name, String publicId, String systemId) {
      _name = name;
      _publicId = publicId;
      _systemId = systemId;
   }

   public String getName() {
      return _name;
   }

   public String getPublicId() {
      return _publicId;
   }

   public String getSystemId() {
      return _systemId;
   }

   public int getEventType() {
      return NOTATION_DECLARATION;
   }

   public void writeAsEncodedUnicode(Writer writer)
      throws XMLStreamException {
      // XXX
   }

   public boolean equals(Object o) {
      if (!(o instanceof NotationDeclaration))
         return false;
      if (o == null)
         return false;
      if (this == o)
         return true;

      NotationDeclaration decl = (NotationDeclaration) o;

      return getName().equals(decl.getName()) &&
         getPublicId().equals(decl.getPublicId()) &&
         getSystemId().equals(decl.getSystemId());
   }
}
