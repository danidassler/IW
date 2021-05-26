package es.ucm.fdi.NewChance.model;

/**
 * Used to json-ize objects
 */
public interface Transferable<T> {
    T toTransfer();
}
