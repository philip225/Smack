Re-coded Smack application.
==========================

For Udemy course Kotlin for Android: Beginner to advanced.
----------------------------------------------------------

Section 7: Smack chat app.
--------------------------

This is not a "normal" application README it goes into details not normally found in a README, for
the purposes of the course. 

This is a re-coded version of this application (section 7) that brings the application up to date 
with the latest changes for a Draw base application.
The UI parts of the course, especially the draw component part is out of date and is (to
the best of my knowledge) no achievable in its current form, anymore.

This version makes use of fragments and navigation, replacing the activities.

This will be added to as I progress through the course, with the completed steps
for section 7 being published in corresponding branches.
Note that some steps will be skipped over.

On completion, the final step will be merged back into master.

The code has detailed comments detailing the changes from the original course, amongst 
other things.

Branches.
---------

All the parts of the course for all the steps that were published, have their own branches
for example "79-Login-user".

The final branch "93-Displaying-Messages" is a complete version but with no tidy-up.
The master branch has a "tidied up" version, with all lint issues for Android, resolved.

Special branches exist:
Resolve-fragment-data-ui-update-issue - this holds the code refactor that was merged back into 
"93-Displaying-Messages" see Bug correction.

Bug correction.
---------------

All the branches up to "93-Displaying-Messages" have a faw in design, they work adequately for 
the course purposes, but the issue required fixing.
It was only discovered late on and required some code refactoring to resolve, hence not porting
the fix, back to the other branches.

The issue:

I ignored (at my later peril) the MVC part and chose not to make use of view models, observables 
and live data, not understanding that there was a ghastly catch in ignoring this!
The ghastly catch came to light when testing for part 93, found that non of the UI elements in
fragment_main updated any more (except the adapter), after navigating away. Also calling 
scrollToPosition() failed with a dreaded null pointer error, when called on messageListView.
What had happened, as far as I could tell the synthetic imports were linked up initially, so 
everything appeared to work. Unfortunately the synthetics were not re-assigned to the new instance 
of the fragment, leaving them connected to a detached fragment (I believe the detachment was cause 
of the null pointer, when calling scrollToPosition()).
 
The fix:

Use live data and observables as strongly hinted at by Android studio's project generation, for
fragments and Navigation Draw Activity!
So split of "93-Displaying-Messages" into branch "Resolve-fragment-data-ui-update-issue" and
re-factored the code to use live updates.
This was then re-merged back into "93-Displaying-Messages".
