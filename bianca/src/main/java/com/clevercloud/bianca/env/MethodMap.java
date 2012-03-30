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
package com.clevercloud.bianca.env;

import com.clevercloud.bianca.function.AbstractFunction;
import com.clevercloud.bianca.function.FunSpecialCall;
import com.clevercloud.bianca.program.ClassDef;
import com.clevercloud.util.L10N;
import com.clevercloud.util.Primes;

import java.util.Iterator;

/**
 * Case-insensitive method mapping
 */
public final class MethodMap<V> {

   private static final L10N L = new L10N(MethodMap.class);
   private final BiancaClass _biancaClass;
   private final ClassDef _classDef;
   private Entry<V>[] _entries = new Entry[16];
   private int _prime = Primes.getBiggestPrime(_entries.length);
   private int _size;

   public MethodMap(BiancaClass biancaClass, ClassDef classDef) {
      _biancaClass = biancaClass;
      _classDef = classDef;
   }

   public void put(String methodName, V value) {
      StringValue name = MethodIntern.intern(methodName);

      if (_entries.length <= _size * 4) {
         resize();
      }

      int hash = name.hashCodeCaseInsensitive();

      int bucket = (hash & 0x7fffffff) % _prime;

      Entry<V> entry;
      for (entry = _entries[bucket]; entry != null; entry = entry.getNext()) {
         StringValue entryKey = entry.getKey();

         if (name == entryKey || name.equalsIgnoreCase(entryKey)) {
            entry.setValue(value);

            return;
         }
      }

      entry = new Entry<V>(name, value);

      entry._next = _entries[bucket];
      _entries[bucket] = entry;
      _size++;

   }

   public boolean containsKey(StringValue key) {
      int hash = key.hashCodeCaseInsensitive();

      final int bucket = (hash & 0x7fffffff) % _prime;

      for (Entry<V> entry = _entries[bucket];
           entry != null;
           entry = entry.getNext()) {
         final StringValue entryKey = entry.getKey();

         if (key == entryKey || key.equalsIgnoreCase(entryKey)) {
            return true;
         }
      }

      return false;
   }

   public final V get(final StringValue key, int hash) {
      final int bucket = (hash & 0x7fffffff) % _prime;

      for (Entry<V> entry = _entries[bucket];
           entry != null;
           entry = entry.getNext()) {
         final StringValue entryKey = entry.getKey();

         if (key == entryKey || key.equalsIgnoreCase(entryKey)) {
            return entry._value;
         }
      }

      AbstractFunction call = null;

      if (_biancaClass != null) {
         call = _biancaClass.getCall();
      } else if (_classDef != null) {
         call = _classDef.getCall();
      }

      if (call != null) {
         return (V) new FunSpecialCall(call, key);
      }

      Env env = Env.getCurrent();

      if (_biancaClass != null) {
         env.error(L.l("Call to undefined method {0}::{1}",
            _biancaClass.getName(), key));
      } else {
         env.error(L.l("Call to undefined function {0}",
            key));
      }

      throw new IllegalStateException();
   }

   public V getRaw(StringValue key) {
      int hash = key.hashCodeCaseInsensitive();

      int bucket = (hash & 0x7fffffff) % _prime;

      for (Entry<V> entry = _entries[bucket];
           entry != null;
           entry = entry.getNext()) {
         StringValue entryKey = entry.getKey();

         if (key == entryKey || key.equalsIgnoreCase(entryKey)) {
            return entry.getValue();
         }
      }

      return null;
   }

   public V get(StringValue key) {
      return get(key, key.hashCodeCaseInsensitive());
   }

   public Iterable<V> values() {
      return new ValueIterator(_entries);
   }

   private boolean match(char[] a, char[] b, int length) {
      if (a.length != length) {
         return false;
      }

      for (int i = length - 1; i >= 0; i--) {
         int chA = a[i];
         int chB = b[i];

         if (chA == chB) {
         } /*
         else if ((chA & ~0x20) != (chB & ~0x20))
         return false;
          */ else {
            if ('A' <= chA && chA <= 'Z') {
               chA += 'a' - 'A';
            }

            if ('A' <= chB && chB <= 'Z') {
               chB += 'a' - 'A';
            }

            if (chA != chB) {
               return false;
            }
         }
      }

      return true;
   }

   private void resize() {
      Entry<V>[] newEntries = new Entry[2 * _entries.length];
      int newPrime = Primes.getBiggestPrime(newEntries.length);

      for (int i = 0; i < _entries.length; i++) {
         Entry<V> entry = _entries[i];

         while (entry != null) {
            Entry<V> next = entry.getNext();

            int hash = entry._key.hashCodeCaseInsensitive();
            int bucket = (hash & 0x7fffffff) % newPrime;

            entry.setNext(newEntries[bucket]);
            newEntries[bucket] = entry;

            entry = next;
         }
      }

      _entries = newEntries;
      _prime = newPrime;
   }

   final static class Entry<V> {

      private final StringValue _key;
      private V _value;
      private Entry<V> _next;

      Entry(StringValue key, V value) {
         _key = key;
         _value = value;
      }

      public final StringValue getKey() {
         return _key;
      }

      public final V getValue() {
         return _value;
      }

      public void setValue(V value) {
         _value = value;
      }

      public Entry<V> getNext() {
         return _next;
      }

      public void setNext(Entry<V> next) {
         _next = next;
      }
   }

   final static class ValueIterator<V> implements Iterable<V>, Iterator<V> {

      int _index;
      Entry<V>[] _entries;
      Entry<V> _next;

      public ValueIterator(Entry<V>[] entries) {
         _entries = entries;

         getNext();
      }

      private void getNext() {
         Entry<V> entry = _next == null ? null : _next._next;

         while (entry == null
            && _index < _entries.length
            && (entry = _entries[_index++]) == null) {
         }

         _next = entry;
      }

      @Override
      public boolean hasNext() {
         return _next != null;
      }

      @Override
      public V next() {
         V value = _next._value;

         getNext();

         return value;
      }

      @Override
      public Iterator<V> iterator() {
         return this;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
