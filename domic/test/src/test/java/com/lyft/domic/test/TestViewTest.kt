package com.lyft.domic.test

import com.jakewharton.rxrelay2.PublishRelay
import com.lyft.domic.api.View
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/* Meta meta meta meta */
// TODO make abstract and use for all TestViews.
class TestViewTest {

    private val testView = TestView()

    @Test
    fun checkActivated_nullByDefault() {
        assertThat(testView.check.activated).isNull()
    }

    @Test
    fun checkAlpha_nullByDefault() {
        assertThat(testView.check.alpha).isNull()
    }

    @Test
    fun checkEnabled_nullByDefault() {
        assertThat(testView.check.enabled).isNull()
    }

    @Test
    fun checkFocus_nullByDefault() {
        assertThat(testView.check.focus).isNull()
    }

    @Test
    fun checkFocusableInTouchMode_nullByDefault() {
        assertThat(testView.check.focusableInTouchMode).isNull()
    }

    @Test
    fun checkVisibility_nullByDefault() {
        assertThat(testView.check.visibility).isNull()
    }

    @Test
    fun changeActivated() {
        val activated = PublishRelay.create<Boolean>()
        testView.change.activated(activated)

        activated.accept(true)
        assertThat(testView.check.activated).isTrue()

        activated.accept(false)
        assertThat(testView.check.activated).isFalse()
    }

    @Test
    fun changeAlpha() {
        val alpha = PublishRelay.create<Float>()
        testView.change.alpha(alpha)

        alpha.accept(0.0f)
        assertThat(testView.check.alpha).isEqualTo(0.0f)

        alpha.accept(1.0f)
        assertThat(testView.check.alpha).isEqualTo(1.0f)
    }

    @Test
    fun changeEnabled() {
        val enabled = PublishRelay.create<Boolean>()
        testView.change.enabled(enabled)

        enabled.accept(true)
        assertThat(testView.check.enabled).isTrue()

        enabled.accept(false)
        assertThat(testView.check.enabled).isFalse()
    }

    @Test
    fun changeFocusable() {
        val focusable = PublishRelay.create<Boolean>()
        testView.change.focusable(focusable)

        focusable.accept(true)
        assertThat(testView.check.focusable).isTrue()

        focusable.accept(false)
        assertThat(testView.check.focusable).isFalse()
    }

    @Test
    fun changeFocusableInTouchMode() {
        val focusableInTouchMode = PublishRelay.create<Boolean>()
        testView.change.focusableInTouchMode(focusableInTouchMode)

        focusableInTouchMode.accept(true)
        assertThat(testView.check.focusableInTouchMode).isTrue()

        focusableInTouchMode.accept(false)
        assertThat(testView.check.focusableInTouchMode).isFalse()
    }

    @Test
    fun changeVisibility() {
        val visibility = PublishRelay.create<View.Visibility>()
        testView.change.visibility(visibility)

        View.Visibility.values().forEach { visibilityValue ->
            visibility.accept(visibilityValue)
            assertThat(testView.check.visibility).isEqualTo(visibilityValue)
        }
    }

    @Test
    fun simulateClick() {
        val observer = testView.observe.clicks.test()

        testView.simulate.click()
        observer.assertValueCount(1)

        testView.simulate.click()
        observer.assertValueCount(2)

        observer.assertNotTerminated()
    }

    @Test
    fun simulateFocus() {
        val observer = testView.observe.focus.test()

        testView.simulate.focus(true)
        observer.assertValueCount(1)

        testView.simulate.focus(true)
        observer.assertValueCount(1)

        testView.simulate.focus(false)
        observer.assertValueCount(2)

        testView.simulate.focus(false)
        observer.assertValueCount(2)

        observer.assertNotTerminated()
    }

    @Test
    fun simulateLongClick() {
        val observer = testView.observe.longClicks.test()

        testView.simulate.longClick()
        observer.assertValueCount(1)

        testView.simulate.longClick()
        observer.assertValueCount(2)

        observer.assertNotTerminated()
    }
}
