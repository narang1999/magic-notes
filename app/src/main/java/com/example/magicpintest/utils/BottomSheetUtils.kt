package com.example.magicpintest.utils

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

fun DialogFragment.showDialog(
    childFragmentManager: FragmentManager,
    TAG: String? =" BottomSheet"
) {
    if (childFragmentManager.findFragmentByTag(TAG) == null)
        show(childFragmentManager, TAG)
}
