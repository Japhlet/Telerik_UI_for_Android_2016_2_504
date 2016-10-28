package com.telerik.widget.list;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * When this ItemAnimator is used items that are added or removed items will fade in or out.
 */
public class FadeItemAnimator extends ListViewItemAnimator {

    private static final float DEFAULT_ALPHA = 0.0f;

    private float alpha = DEFAULT_ALPHA;

    /**
     * Creates a new instance of FadeItemAnimator.
     */
    public FadeItemAnimator() {
    }

    /**
     * Gets the current alpha value that is used by the animator.
     * It is the start value for items which are added and it is also
     * the end value for items which are removed.
     *
     * Default is <code>0.0f</code>
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets a new alpha value that is used by the animator.
     * It is the start value for items which are added and it is also
     * the end value for items which are removed.
     *
     * Default is <code>0.0f</code>
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    protected void animateViewAddedPrepare(RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            return;
        }

        ViewCompat.setAlpha(holder.itemView, alpha);
    }

    @Override
    protected ViewPropertyAnimatorCompat addAnimation(final RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            return super.addAnimation(holder);
        }

        final View animatedView = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(animatedView);
        ViewPropertyAnimatorCompat addAnimation = animation
                .alpha(1)
                .setDuration(getAddDuration());
        return addAnimation;
    }

    @Override
    protected void onAnimationAddCancelled(RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            return;
        }

        ViewCompat.setAlpha(holder.itemView, 1);
    }

    @Override
    protected void onAnimationAddEnded(ViewPropertyAnimatorCompat animation, RecyclerView.ViewHolder holder) {
        super.onAnimationAddEnded(animation, holder);
        if((getType() & ADD) != ADD) {
            return;
        }

        ViewCompat.setAlpha(holder.itemView, 1);
    }

    @Override
    protected ViewPropertyAnimatorCompat removeAnimation(final RecyclerView.ViewHolder holder) {
        if((getType() & REMOVE) != REMOVE) {
            return super.removeAnimation(holder);
        }

        View animatedView = holder.itemView;

        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(animatedView);
        ViewPropertyAnimatorCompat removeAnimation = animation
                .setDuration(getRemoveDuration())
                .alpha(alpha);
        return removeAnimation;
    }

    @Override
    protected void onAnimationRemoveEnded(ViewPropertyAnimatorCompat animation, RecyclerView.ViewHolder holder) {
        super.onAnimationRemoveEnded(animation, holder);

        if((getType() & REMOVE) != REMOVE) {
            return;
        }

        ViewCompat.setAlpha(holder.itemView, 1);
    }
}
