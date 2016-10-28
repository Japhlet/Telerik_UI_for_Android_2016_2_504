package com.telerik.widget.list;

import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows the usage of more than one ListViewItemAnimator at the same time
 * with RadListView.
 */
public class ItemAnimatorSet extends ListViewItemAnimator {
    private List<ListViewItemAnimator> animators = new ArrayList<ListViewItemAnimator>();

    public void addAnimator(ListViewItemAnimator animator) {
        this.animators.add(animator);
    }

    public void removeAnimator(ListViewItemAnimator animator) {
        this.animators.remove(animator);
    }

    public void clearAnimators() {
        this.animators.clear();
    }

    @Override
    public void onAttached(RadListView listView) {
        super.onAttached(listView);
        for(ListViewItemAnimator animator : animators) {
            animator.onAttached(listView);
        }
    }

    @Override
    public void onDetached(RadListView listView) {
        super.onDetached(listView);
        for(ListViewItemAnimator animator : animators) {
            animator.onDetached(listView);
        }
    }

    protected void animateViewRemovedImpl(final RecyclerView.ViewHolder holder) {
        final ViewPropertyAnimatorCompatSet animationSet = removeAnimationSet(holder);
        VpaListenerAdapter listenerAdapter = new VpaListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                onAnimationRemoveStarted(holder);
            }

            @Override
            public void onAnimationCancel(View view) {
                onAnimationRemoveCancelled(holder);
            }

            @Override
            public void onAnimationEnd(View view) {
                onAnimationSetRemoveEnded(animationSet, holder);
            }
        };
        animationSet.setListener(listenerAdapter).start();
        mRemoveAnimations.add(holder);
    }

    @Override
    protected void animateViewAddedPrepare(RecyclerView.ViewHolder holder) {
        boolean animatorExecuted = false;
        for(ListViewItemAnimator animator : animators) {
            animator.animateViewAddedPrepare(holder);
            animatorExecuted = true;
        }
        if(!animatorExecuted) {
            super.animateViewAddedPrepare(holder);
        }
    }

    @Override
    protected void animateViewAddedImpl(final RecyclerView.ViewHolder holder) {
        mAddAnimations.add(holder);
        final ViewPropertyAnimatorCompatSet addAnimationSet = addAnimationSet(holder);
        VpaListenerAdapter listenerAdapter = new VpaListenerAdapter() {

            @Override
            public void onAnimationStart(View view) {
                onAnimationAddStarted(holder);
            }

            @Override
            public void onAnimationCancel(View view) {
                onAnimationSetAddCancelled(holder);
            }

            @Override
            public void onAnimationEnd(View view) {
                onAnimationSetAddEnded(addAnimationSet, holder);
            }
        };
        addAnimationSet.setListener(listenerAdapter).start();
    }

    private ViewPropertyAnimatorCompatSet addAnimationSet(RecyclerView.ViewHolder holder) {
        boolean setIsEmpty = true;
        ViewPropertyAnimatorCompatSet animationSet = new ViewPropertyAnimatorCompatSet();
        for(ListViewItemAnimator animator : animators) {
            if((animator.getType() & ADD) == ADD) {
                animationSet.play(animator.addAnimation(holder));
                setIsEmpty = false;
            }
        }
        if(setIsEmpty) {
            animationSet.play(super.addAnimation(holder));
        }
        return animationSet;
    }

    private ViewPropertyAnimatorCompatSet removeAnimationSet(RecyclerView.ViewHolder holder) {
        boolean setIsEmpty = true;
        ViewPropertyAnimatorCompatSet animationSet = new ViewPropertyAnimatorCompatSet();
        for(ListViewItemAnimator animator : animators) {
            if((animator.getType() & REMOVE) == REMOVE) {
                animationSet.play(animator.removeAnimation(holder));
                setIsEmpty = false;
            }
        }
        if(setIsEmpty) {
            animationSet.play(super.removeAnimation(holder));
        }
        return animationSet;
    }

    private void onAnimationSetAddCancelled(RecyclerView.ViewHolder holder) {
        boolean setIsEmpty = true;
        for(ListViewItemAnimator animator : animators) {
            if((animator.getType() & ADD) == ADD) {
                animator.onAnimationAddCancelled(holder);
                setIsEmpty = false;
            }
        }
        if(setIsEmpty) {
            super.onAnimationAddCancelled(holder);
        }
    }

    private void onAnimationSetAddEnded(ViewPropertyAnimatorCompatSet animationSet, RecyclerView.ViewHolder holder) {
        boolean setIsEmpty = true;
        for(ListViewItemAnimator animator : animators) {
            if((animator.getType() & ADD) == ADD) {
                animator.onAnimationAddEnded(null, holder);
                setIsEmpty = false;
            }
        }
        if(setIsEmpty) {
            super.onAnimationAddEnded(null, holder);
        }
        animationSet.setListener(null);
        dispatchAddFinished(holder);
        mAddAnimations.remove(holder);
        dispatchFinishedWhenDone();
    }

    private void onAnimationSetRemoveEnded(ViewPropertyAnimatorCompatSet animationSet, RecyclerView.ViewHolder holder) {
        boolean setIsEmpty = true;
        for(ListViewItemAnimator animator : animators) {
            if((animator.getType() & REMOVE) == REMOVE) {
                animator.onAnimationRemoveEnded(null, holder);
                setIsEmpty = false;
            }
        }
        if(setIsEmpty) {
            super.onAnimationRemoveEnded(null, holder);
        }
        animationSet.setListener(null);
        dispatchAddFinished(holder);
        mAddAnimations.remove(holder);
        dispatchFinishedWhenDone();
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder holder) {
        super.endAnimation(holder);
        for (ListViewItemAnimator animator : animators) {
            animator.onEndAnimation(holder);
        }
    }
}
