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
package com.clevercloud.bianca.script;

import com.clevercloud.bianca.env.Env;
import com.clevercloud.bianca.env.Value;
import com.clevercloud.bianca.page.BiancaPage;
import com.clevercloud.bianca.page.InterpretedPage;
import com.clevercloud.bianca.program.BiancaProgram;
import com.clevercloud.vfs.NullWriteStream;
import com.clevercloud.vfs.ReaderWriterStream;
import com.clevercloud.vfs.WriteStream;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Writer;

/**
 * Script engine
 */
public class BiancaCompiledScript extends CompiledScript {

   private final BiancaScriptEngine _engine;
   private final BiancaProgram _program;

   BiancaCompiledScript(BiancaScriptEngine engine, BiancaProgram program) {
      _engine = engine;
      _program = program;
   }

   /**
    * evaluates based on a reader.
    */
   @Override
   public Object eval(ScriptContext cxt)
      throws ScriptException {
      Env env = null;

      try {
         Writer writer = cxt.getWriter();

         WriteStream out;

         if (writer != null) {
            ReaderWriterStream s = new ReaderWriterStream(null, writer);
            WriteStream os = new WriteStream(s);

            os.setNewlineString("\n");

            try {
               os.setEncoding("utf-8");
            } catch (Exception e) {
            }

            out = os;
         } else {
            out = new NullWriteStream();
         }

         BiancaPage page = new InterpretedPage(_program);

         env = new Env(_engine.getBianca(), page, out, null, null);

         env.setScriptContext(cxt);

         // php/214g
         env.start();

         Value resultV = _program.execute(env);

         Object result = null;
         if (resultV != null) {
            result = resultV.toJavaObject();
         }

         out.flushBuffer();
         out.free();

         return result;
         /*
         } catch (ScriptException e) {
         throw e;
          */
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new ScriptException(e);
      } catch (Throwable e) {
         throw new RuntimeException(e);
      } finally {
         if (env != null) {
            env.close();
         }
      }
   }

   /**
    * Returns the script engine.
    */
   @Override
   public ScriptEngine getEngine() {
      return _engine;
   }

   @Override
   public String toString() {
      return "BiancaCompiledScript[]";
   }
}
