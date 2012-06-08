package iago.history;

/**
 * Signals that we have left the current opening book game-tree
 *
 */
@SuppressWarnings("serial")
public class UnexploredException extends Exception {
    public UnexploredException() {}
    public UnexploredException(String msg) { super(msg); }
    public UnexploredException(Throwable cause) { super(cause); }
    public UnexploredException(String msg, Throwable cause) { super(msg, cause); }
}
