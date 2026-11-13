package Q4;

import java.util.*;

/**
 * PharmacyInventory
 * Implements hospital pharmacy stock lookup and report generation
 * across HashMap, TreeMap, and LinkedHashMap.
 */
public class PharmacyInventory {

    public enum MapType {
        HASH_MAP,
        TREE_MAP,
        LINKED_HASH_MAP
    }

    private final MapType type;
    private final Map<String, Medicine> stockMap;
    // For TreeMap, because it naturally destroys insertion order,
    // we must maintain secondary sequence metadata to produce Report 2 if required.
    private final List<Medicine> insertionOrderFallbackList;

    public PharmacyInventory(MapType type) {
        this.type = type;
        switch (type) {
            case HASH_MAP:
                this.stockMap = new HashMap<>();
                this.insertionOrderFallbackList = new ArrayList<>();
                break;
            case TREE_MAP:
                this.stockMap = new TreeMap<>();
                this.insertionOrderFallbackList = new ArrayList<>();
                break;
            case LINKED_HASH_MAP:
                this.stockMap = new LinkedHashMap<>();
                this.insertionOrderFallbackList = null; // LinkedHashMap inherently maintains insertion order
                break;
            default:
                throw new IllegalArgumentException("Unknown map type: " + type);
        }
    }

    public void putMedicine(Medicine m) {
        stockMap.put(m.getCode(), m);
        if (insertionOrderFallbackList != null) {
            insertionOrderFallbackList.add(m);
        }
    }

    public Medicine getMedicine(String code) {
        return stockMap.get(code);
    }

    public int size() {
        return stockMap.size();
    }

    /**
     * Report 1: All medicines in CODE ORDER (natural alphanumeric sorting).
     * Naturally produced by TreeMap.
     * HashMap and LinkedHashMap require extra work: copying and sorting (O(N log N)).
     */
    public List<Medicine> generateCodeOrderReport() {
        if (type == MapType.TREE_MAP) {
            // Naturally in code order
            return new ArrayList<>(stockMap.values());
        } else {
            // Extra work required: Copy to list and sort O(N log N)
            List<Medicine> list = new ArrayList<>(stockMap.values());
            list.sort(Comparator.comparing(Medicine::getCode));
            return list;
        }
    }

    /**
     * Report 2: All medicines in INSERTION ORDER (arrival order).
     * Naturally produced by LinkedHashMap.
     * HashMap destroys insertion order completely.
     * TreeMap maintains sorted order, so insertion order is lost unless secondary storage is kept.
     */
    public List<Medicine> generateInsertionOrderReport() {
        if (type == MapType.LINKED_HASH_MAP) {
            // Naturally in insertion order
            return new ArrayList<>(stockMap.values());
        } else if (type == MapType.HASH_MAP) {
            // HashMap iteration is pseudo-random bucket order; relies on fallback or returns unordered
            if (insertionOrderFallbackList != null) {
                return new ArrayList<>(insertionOrderFallbackList);
            }
            return new ArrayList<>(stockMap.values()); // Order broken!
        } else { // TREE_MAP
            // TreeMap values are code-sorted; relies on fallback or returns code order
            if (insertionOrderFallbackList != null) {
                return new ArrayList<>(insertionOrderFallbackList);
            }
            return new ArrayList<>(stockMap.values()); // Order broken!
        }
    }

    /**
     * Raw iteration order directly from map.values() without any adaptation.
     */
    public List<Medicine> getRawIterationList() {
        return new ArrayList<>(stockMap.values());
    }

    public MapType getType() {
        return type;
    }
}

