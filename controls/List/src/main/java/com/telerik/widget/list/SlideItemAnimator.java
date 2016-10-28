package com.telerik.widget.list;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * When this ItemAnimator is used items that are added or removed items will slide from and to the edges.
 */
public class SlideItemAnimator extends ListViewItemAnimator {

    public static final int DIRECTION_LEFT = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_BOTTOM = 3;

    private static final int DEFAULT_DIRECTION = DIRECTION_RIGHT;

    private float originalTranslateX = 0;
    private float originalTranslateY = 0;

    private int animateInDirection = DEFAULT_DIRECTION;
    private int animateOutDirection = DEFAULT_DIRECTION;

    /**
     * Creates a new instance of SlideItemAnimator.
     */
    public SlideItemAnimator() {
    }

    /**
     * Gets the current animate in direction that is used by the animator.
     * The animate in direction is used for start position of items that are added to the list.
     *
     * Default is <code>DIRECTION_RIGHT</code>
     */
    public int getAnimateInDirection() {
        return animateInDirection;
    }

    /**
     * Sets a new animate in direction that is used by the animator.
     * The animate in direction is used for start position of items that are added to the list.
     *
     * Default is <code>DIRECTION_RIGHT</code>
     */
    public void setAnimateInDirection(int animateInDirection) {
        this.animateInDirection = animateInDirection;
    }

    /**
     * Gets the current animate out direction that is used by the animator.
     * The animate out direction is used for end position of items that are removed from the list.
     *
     * Default is <code>DIRECTION_RIGHT</code>
     */
    public int getAnimateOutDirection() {
        return animateOutDirection;
    }

    /**
     * Sets a new animate out direction that is used by the animator.
     * The animate out direction is used for end position of items that are removed from the list.
     *
     * Default is <code>DIRECTION_RIGHT</code>
     */
    public void setAnimateOutDirection(int animateOutDirection) {
        this.animateOutDirection = animateOutDirection;
    }

    @Override
    protected void animateViewAddedPrepare(RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            return;
        }

        View animatedView = holder.itemView;

        float translationValueX = calculateTranslationValue(this.animateInDirection, holder, true);
        originalTranslateX = ViewCompat.getTranslationX(animatedView);
        ViewCompat.setTranslationX(animatedView, originalTranslateX + translationValueX);

        float translationValueY = calculateTranslationValue(this.animateInDirection, holder, false);
        originalTranslateY = ViewCompat.getTranslationY(animatedView);
        ViewCompat.setTranslationY(animatedView, originalTranslateY + translationValueY);
    }

    @Override
    protected ViewPropertyAnimatorCompat addAnimation(final RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            return super.addAnimation(holder);
        }

        final View animatedView = holder.itemView;
        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(animatedView);
        ViewPropertyAnimatorCompat addAnimation = animation
                .translationX(originalTranslateX)
                .translationY(originalTranslateY)
                .setDuration(getAddDuration());
        return addAnimation;
    }

    @Override
    protected void onAnimationAddCancelled(RecyclerView.ViewHolder holder) {
        if((getType() & ADD) != ADD) {
            super.onAnimationAddCancelled(holder);
            return;
        }

        View view = holder.itemView;
        ViewCompat.setTranslationX(view, originalTranslateX);
        ViewCompat.setTranslationY(view, originalTranslateY);
    }

    @Override
    protected void onAnimationAddEnded(ViewPropertyAnimatorCompat animation, RecyclerView.ViewHolder holder) {
        super.onAnimationAddEnded(animation, holder);
        if((getType() & ADD) != ADD) {
            return;
        }

        View view = holder.itemView;
        ViewCompat.setTranslationX(view, originalTranslateX);
        ViewCompat.setTranslationY(view, originalTranslateY);
    }

    @Override
    protected ViewPropertyAnimatorCompat removeAnimation(final RecyclerView.ViewHolder holder) {
        if((getType() & REMOVE) != REMOVE) {
            return super.removeAnimation(holder);
        }

        View animatedView = holder.itemView;

        final float translationValueX = calculateTranslationValue(this.animateOutDirection, holder, true);
        final float translationValueY = calculateTranslationValue(this.animateOutDirection, holder, false);

        originalTranslateX = ViewCompat.getTranslationX(animatedView);
        originalTranslateY = ViewCompat.getTranslationY(animatedView);

        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(animatedView);
        ViewPropertyAnimatorCompat removeAnimation = animation
                .setDuration(getRemoveDuration())
                .translationX(originalTranslateX + translationValueX)
                .translationY(originalTranslateY + translationValueY);
        return removeAnimation;
    }

    @Override
    protected void onAnimationRemoveEnded(ViewPropertyAnimatorCompat animation, RecyclerView.ViewHolder holder) {
        super.onAnimationRemoveEnded(animation, holder);

        if((getType() & REMOVE) != REMOVE) {
            return;
        }

        View view = holder.itemView;

        ViewCompat.setTranslationX(view, originalTranslateX);
        ViewCompat.setTranslationY(view, originalTranslateY);
    }

    private float calculateTranslationValue(int direction, RecyclerView.ViewHolder holder, boolean isHorizontal) {
        if(holder == null || holder.itemView == null) {
            return 0;
        }
        switch (direction) {
            case DIRECTION_LEFT:
                return isHorizontal ? -holder.itemView.getLeft()-holder.itemView.getWidth() : 0;
            case DIRECTION_TOP:
                return isHorizontal ? 0 : -holder.itemView.getTop()-holder.itemView.getHeight();
            case DIRECTION_RIGHT:
                return isHorizontal ? owner.getWidth() : 0;
            case DIRECTION_BOTTOM:
                return isHorizontal ? 0 : owner.getHeight();
        }
        return 0;
    }
}
