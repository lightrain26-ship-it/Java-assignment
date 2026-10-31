# Java-assignment
just trying to type something here

FileMenu: all file names
ReadFunction: reading used
FileFunction: read permissions and choosing a file
WriteFunction: write permission, add/update/delete,logging
WriteFile: Low-level writing
DATA RULES
the data folder sits in the project root
the separator is |
row 0 of every file is the header
the ID is always in the first column, and must be unique
use N/A instead of leaving a value empty
values must not contain |
always use FileMenu constants, never type file names by hand

role name
"Managers", "Administrators", "Instructors", "Facilities Officers", "Attendance Officers", "Students". Note that they’re case-sensitive.

rules:
lways write through WriteFunction, never WriteFile directly (otherwise permissions are skipped)
logging is the role’s job: call logChange after a successful add/update/delete, with your own row format
Students have no log

Permissions table (optional, but very useful)
Roles down the side, files across, with R / W marks. It answers “can my role touch this file?” at a glance.

Known limitations

the methods print to the console for now, which will change for the GUI
deleting a record doesn’t delete linked data in other files
anything still undecided (like updateRecord)

Before writing it, sort out these two, so you don’t document something that changes right after:

updateRecord: decide its final form first (full list vs. one new row)
CanRead / CanWrite aren’t public, so teammates in other packages can’t call them. Decide whether they should be, or whether teammates only use them indirectly.