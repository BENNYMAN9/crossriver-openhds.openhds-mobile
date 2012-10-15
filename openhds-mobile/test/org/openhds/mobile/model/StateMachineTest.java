package org.openhds.mobile.model;

import junit.framework.TestCase;

import org.openhds.mobile.model.StateMachine.State;
import org.openhds.mobile.model.StateMachine.StateListener;

public class StateMachineTest extends TestCase {

    public void testShouldMoveToNextState() {
        StateMachine machine = new StateMachine();
        machine.transitionTo(State.SELECT_SUBREGION);

        assertEquals(State.SELECT_SUBREGION, machine.getState());
    }

    public void testShouldFireListenerOnEnterState() {
        StateMachine sm = new StateMachine();
        EnterStateListener listener = new EnterStateListener();
        sm.registerListener(State.SELECT_SUBREGION, listener);

        sm.transitionTo(State.SELECT_SUBREGION);

        assertTrue(listener.fired);
    }

    static class EnterStateListener implements StateListener {
        boolean fired = false;

        public void onEnterState() {
            fired = true;
        }

        public void onLeaveState() {

        }

    }

    public void testShouldFireListenerOnExitState() {
        StateMachine sm = new StateMachine();
        ExitStateListener listener = new ExitStateListener();
        sm.registerListener(State.SELECT_REGION, listener);

        sm.transitionTo(State.SELECT_SUBREGION);

        assertTrue(listener.fired);
    }

    static class ExitStateListener implements StateListener {
        boolean fired = false;

        public void onEnterState() {
        }

        public void onLeaveState() {
            fired = true;
        }

    }

}
