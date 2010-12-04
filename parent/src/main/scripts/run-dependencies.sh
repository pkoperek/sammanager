#!/bin/bash
#
# This file is part of SAMM.
#
# SAMM is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# SAMM is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
#

#
# ď»żThis file is part of SAMM.
#
# SAMM is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# SAMM is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
#


# Settings
PIDS_TO_KILL_FILE=___PIDS_TO_KILL
PIDS_BEFORE_FILE=__pids_before
PIDS_BEFORE_TMP_FILE=__pids_before_tmp
PIDS_AFTER_FILE=__pids_after
PIDS_AFTER_TMP_FILE=__pids_after_tmp

# functions
function add_pids_from_diff() {
	PIDS_TO_KILL=`diff $1 $2 | tail -n +2 | awk '{print $2}'`

	for I in $PIDS_TO_KILL; do
		echo $I >> $PIDS_TO_KILL_FILE
	done;
}

function list_pids() {
	# $1 - what to search for
	# $2 - file for pids
	# $3 - tmp file
	PIDS=`ps -eo pid,args|grep $1|grep -v grep|awk '{print $1}'`

	rm -f $2 $3
	touch $2
	for I in $PIDS; do
		echo $I >> $2
	done;
	sort $2 >& $3
}

# cleanup
rm -f $PIDS_TO_KILL_FILE

# run dependencies 

# run hsqldb
echo "Starting: hsqldb..."

. hsqldbcp.sh
echo "Using: $CLASSPATH"

list_pids org.hsqldb.Server $PIDS_BEFORE_FILE $PIDS_BEFORE_TMP_FILE

java -cp $CLASSPATH org.hsqldb.Server -database.0 file:sammdb -dbname.0 SAMM >& hsqldb.output&

sleep 3
list_pids org.hsqldb.Server $PIDS_AFTER_FILE $PIDS_AFTER_TMP_FILE
add_pids_from_diff $PIDS_AFTER_TMP_FILE $PIDS_BEFORE_TMP_FILE

echo "Started: hsqldb..."
