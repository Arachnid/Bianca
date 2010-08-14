/*
 * Copyright (c) 1998-2010 Caucho Technology -- all rights reserved
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

package javax.persistence.criteria;

import java.util.Collection;
import java.util.Map;

import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * A path expression.
 *
 * @since JPA 2.0
 */
public interface Path<X> extends Expression<X> {
  public Bindable<X> getModel();
  
  public Path<?> getParentPath();
  
  public <Y> Path<Y> get(SingularAttribute<? super X,Y> attribute);
  
  public <E,C extends Collection<E>> Expression<C>
  get(PluralAttribute<X,C,E> collection);
  
  public <K,V,M extends Map<K,V>> Expression<M>
  get(MapAttribute<X,K,V> map);
  
  public Expression<Class<? extends X>> type();
  
  public <Y> Path<Y> get(String attributeName);
}
