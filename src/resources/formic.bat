@echo off
rem Formic installer framework.
rem Copyright (C) 2004 - 2006  Eric Van Dewoestine
rem
rem This library is free software; you can redistribute it and/or
rem modify it under the terms of the GNU Lesser General Public
rem License as published by the Free Software Foundation; either
rem version 2.1 of the License, or (at your option) any later version.
rem
rem This library is distributed in the hope that it will be useful,
rem but WITHOUT ANY WARRANTY; without even the implied warranty of
rem MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
rem Lesser General Public License for more details.
rem
rem You should have received a copy of the GNU Lesser General Public
rem License along with this library; if not, write to the Free Software
rem Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

rem Author: Eric Van Dewoestine

rem Script executed to run the installer.

set BUILD_FILE=%1.xml

set CUR_PATH=%~dp0

pushd "%CUR_PATH%"

rem FORMIC_OPTS=-Djava.ext.dirs
set ANT_OPTS=-Djava.library.path=ant/lib/native/windows %FORMIC_OPTS%
set ANT_HOME=%CUR_PATH%\ant
echo "ANT_OPTS=%ANT_OPTS%"
echo "ANT_HOME=%ANT_HOME%"
echo "ANT=ant\bin\ant -logger org.formic.ant.logger.Log4jLogger -lib . -f %BUILD_FILE% %*"

set CLASSPATH=
ant\bin\ant -logger org.formic.ant.logger.Log4jLogger -lib . -f %BUILD_FILE% %*
set EXIT_CODE=%ERRORLEVEL%

popd

exit %EXIT_CODE%
