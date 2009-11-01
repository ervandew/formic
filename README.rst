.. Formic installer framework.
   Copyright (C) 2005 - 2008  Eric Van Dewoestine

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Welcome to Formic
==================

.. _overview:

==================
Overview
==================

Formic is an installation framework built on top of apache's ant_. The idea is
to leverage the vast amount of functionality that ant provides (directly and
indirectly through third party add-ons) to create a framework based on a
paradigm that the entire ant user base is familiar and comfortable with.

Formic consists of a set of ant tasks for packaging and running graphical and
console based installers. The entire process of creating an installer with
formic can be summed up in the following three steps.

* Create a series of panels (or utilize the formic supplied panels) for the
  installation wizard.

* Create an ant script to be executed by the installer for install and
  un-install of your application, which describes the flow of the installation
  wizard plus the series of ant tasks necessary to (un)install your application
  on the host system.

* Package your application files, installation build file, and a stripped down
  version of ant into platform specific executables.

.. _license:

==================
License
==================

Formic is released under the terms of the LGPL_.

.. _LGPL: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
.. _ant: http://ant.apache.org/
.. _Downloads: http://sourceforge.net/project/showfiles.php?group_id=161801
