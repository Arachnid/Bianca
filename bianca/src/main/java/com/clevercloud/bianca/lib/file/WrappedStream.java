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
 * @author Marc-Antoine Perennou <Marc-Antoine@Perennou.com>
 */
package com.clevercloud.bianca.lib.file;

import com.clevercloud.bianca.BiancaModuleException;
import com.clevercloud.bianca.env.*;
import com.clevercloud.vfs.TempBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A stream that has its operations mediated by a Bianca object.
 */
public class WrappedStream implements BinaryInput, BinaryOutput {

   private static final StringValue STREAM_CLOSE = new StringValue("stream_close");
   private static final StringValue STREAM_EOF = new StringValue("stream_eof");
   private static final StringValue STREAM_FLUSH = new StringValue("stream_flush");
   private static final StringValue STREAM_OPEN = new StringValue("stream_open");
   private static final StringValue STREAM_READ = new StringValue("stream_read");
   private static final StringValue STREAM_SEEK = new StringValue("stream_seek");
   private static final StringValue STREAM_TELL = new StringValue("stream_tell");
   private static final StringValue STREAM_WRITE = new StringValue("stream_write");
   private byte[] printBuffer = new byte[1];
   private Env _env;
   private Value _wrapper;
   private LineReader _lineReader;
   private InputStream _is;
   private OutputStream _os;
   private int _buffer;
   private boolean _doUnread = false;
   private int _writeLength;

   private WrappedStream(Env env, Value wrapper) {
      _env = env;

      _wrapper = wrapper;

      _lineReader = new LineReader(env);
   }

   public WrappedStream(Env env,
                        BiancaClass qClass,
                        StringValue path,
                        StringValue mode,
                        LongValue options) {
      _env = env;

      _lineReader = new LineReader(env);

      _wrapper = qClass.callNew(_env, new Value[0]);

      _wrapper.callMethod(_env, STREAM_OPEN,
         path, mode, options, NullValue.NULL);
   }

   @Override
   public InputStream getInputStream() {
      if (_is == null) {
         _is = new WrappedInputStream();
      }

      return _is;
   }

   @Override
   public OutputStream getOutputStream() {
      if (_os == null) {
         _os = new WrappedOutputStream();
      }

      return _os;
   }

   /**
    * Opens a new copy.
    */
   @Override
   public BinaryInput openCopy()
      throws IOException {
      return new WrappedStream(_env, _wrapper);
   }

   /**
    * Sets the current read encoding.  The encoding can either be a
    * Java encoding name or a mime encoding.
    *
    * @param encoding name of the read encoding
    */
   public void setEncoding(String encoding)
      throws UnsupportedEncodingException {
   }

   @Override
   public void closeRead() {
      close();
   }

   @Override
   public void closeWrite() {
      close();
   }

   @Override
   public void close() {
      _wrapper.callMethod(_env, STREAM_CLOSE);
   }

   /**
    * Reads a character from a file, returning -1 on EOF.
    */
   /* TOCHECK */
   @Override
   public int read()
      throws IOException {
      if (_doUnread) {
         _doUnread = false;

         return _buffer;
      } else {
         Value output;

         output = _wrapper.callMethod(_env, STREAM_READ, LongValue.ONE);

         _buffer = (int) output.toLong();

         return _buffer;
      }
   }

   /**
    * Unread a character.
    */
   @Override
   public void unread()
      throws IOException {
      _doUnread = true;
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      // TODO: shgould be reimplemented

      Value output;

      output = _wrapper.callMethod(_env, STREAM_READ,
         LongValue.create(length));

      // XXX "0"?

      if (!output.toBoolean()) {
         return -1;
      }

      byte[] outputBytes = output.toString().getBytes();

      if (outputBytes.length < length) {
         length = outputBytes.length;
      }

      System.arraycopy(outputBytes, 0, buffer, offset, length);

      return length;
   }

   public int read(char[] buffer, int offset, int length) {
      // TODO: should be reimplemented

      Value output;

      output = _wrapper.callMethod(_env, STREAM_READ,
         LongValue.create(length));

      // XXX "0"?

      if (!output.toBoolean()) {
         return -1;
      }

      byte[] outputBytes = output.toString().getBytes();

      if (outputBytes.length < length) {
         length = outputBytes.length;
      }

      System.arraycopy(outputBytes, 0, buffer, offset, length);

      return length;
   }

   /**
    * Appends to a string builder.
    */
   @Override
   public StringValue appendTo(StringValue builder) {
      try {
         int ch;

         while ((ch = read()) >= 0) {
            builder.append((char) ch);
         }

         return builder;
      } catch (IOException e) {
         throw new BiancaModuleException(e);
      }
   }

   /**
    * Reads a Binary string.
    */
   @Override
   public StringValue read(int length)
      throws IOException {
      Value output;

      output = _wrapper.callMethod(_env, STREAM_READ,
         LongValue.create(length));

      return output.toStringValue(_env);
   }

   /**
    * Reads the optional linefeed character from a \r\n
    */
   @Override
   public boolean readOptionalLinefeed()
      throws IOException {
      int ch = read();

      if (ch == '\n') {
         return true;
      } else {
         unread();
         return false;
      }
   }

   /**
    * Reads a line from a file, returning null on EOF.
    */
   @Override
   public StringValue readLine(long length)
      throws IOException {
      return _lineReader.readLine(_env, this, length);
   }

   @Override
   public void write(byte[] buffer, int offset, int length)
      throws IOException {
      StringValue bb = new StringValue(new String(buffer), offset, length);

      Value output;

      output = _wrapper.callMethod(_env, STREAM_WRITE, bb);

      _writeLength = (int) output.toLong();
   }

   /**
    * Writes to a stream.
    */
   @Override
   public int write(InputStream is, int length) {
      int writeLength = 0;

      TempBuffer tb = TempBuffer.allocate();
      byte[] buffer = tb.getBuffer();

      try {
         while (length > 0) {
            int sublen;

            if (length < buffer.length) {
               sublen = length;
            } else {
               sublen = buffer.length;
            }

            sublen = is.read(buffer, 0, sublen);

            if (sublen < 0) {
               break;
            }

            for (int offset = 0; offset < sublen; ) {
               write(buffer, offset, sublen);

               if (_writeLength > 0) {
                  offset += _writeLength;
               } else {
                  return writeLength;
               }
            }

            writeLength += sublen;
            length -= sublen;
         }

         return writeLength;
      } catch (IOException e) {
         throw new BiancaModuleException(e);
      } finally {
         TempBuffer.free(tb);
      }
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(char v)
      throws IOException {
      printBuffer[0] = (byte) v;

      write(printBuffer, 0, 1);
   }

   /**
    * Prints a string to a file.
    */
   @Override
   public void print(String v)
      throws IOException {
      for (int i = 0; i < v.length(); i++) {
         print(v.charAt(i));
      }
   }

   /**
    * Returns true if end-of-file has been reached
    */
   @Override
   public boolean isEOF() {
      return _wrapper.callMethod(_env, STREAM_EOF).toBoolean();
   }

   /**
    * Tells the position in the stream
    */
   @Override
   public long getPosition() {
      return _wrapper.callMethod(_env, STREAM_TELL).toLong();
   }

   /**
    * Sets the position.
    */
   @Override
   public boolean setPosition(long offset) {
      LongValue offsetValue = LongValue.create(offset);
      LongValue whenceValue = LongValue.create(SEEK_SET);

      return _wrapper.callMethod(_env, STREAM_SEEK,
         offsetValue, whenceValue).toBoolean();
   }

   @Override
   public long seek(long offset, int whence) {
      LongValue offsetValue = LongValue.create(offset);
      LongValue whenceValue = LongValue.create(whence);

      return _wrapper.callMethod(_env, STREAM_SEEK,
         offsetValue, whenceValue).toLong();
   }

   @Override
   public void flush()
      throws IOException {
      boolean result;

      result = _wrapper.callMethod(_env, STREAM_FLUSH).toBoolean();

      if (!result) {
         throw new IOException(); // Get around java.io.Flushable
      }
   }

   @Override
   public Value stat() {
      return _wrapper.callMethod(_env, STREAM_FLUSH);
   }

   private class WrappedInputStream extends InputStream {

      @Override
      public int read()
         throws IOException {
         return WrappedStream.this.read();
      }
   }

   private class WrappedOutputStream extends OutputStream {

      @Override
      public void write(int b)
         throws IOException {
         _wrapper.callMethod(_env, STREAM_WRITE, LongValue.create(b));
      }
   }
}
