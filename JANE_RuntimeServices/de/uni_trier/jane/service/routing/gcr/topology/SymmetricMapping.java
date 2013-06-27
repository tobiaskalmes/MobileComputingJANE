package de.uni_trier.jane.service.routing.gcr.topology;

import java.util.*;

/**
 * This class implements a mapping from keys to a set of values. Each key points
 * to a set of values, while each value has a set of keys pointing to that
 * value. In other words, the class implements the following two symmetric
 * mappings which expresses an n to m relation between keys and values.
 * <ul>
 * <li>key -> {values}</li>
 * <li>value -> {keys}</li>
 * </ul>
 * 
 * @author Hannes Frey
 */
public class SymmetricMapping {

	// the mapping from keys to value sets
	private Map keyValueMap;
	
	// the mapping from values to key sets
	private Map valueKeyMap;

	/**
	 * Construct a new <code>SymmetricMapping</code> object.
	 */
	public SymmetricMapping() {
		keyValueMap = new HashMap();
		valueKeyMap = new HashMap();
	}

	public String toString() {
		return "keyValueMap=" + keyValueMap.toString() + ", valueKeyMap=" + valueKeyMap.toString();
	}
	
	/**
	 * Get all known keys. The returned set may not be modified.
	 * The method returns an empty set instead of <code>null</code> when
	 * the mappings are empty.
	 * @return the set of keys
	 */
	public Set getKeys() {
		Set s = keyValueMap.keySet();
		return Collections.unmodifiableSet(s == null ? new LinkedHashSet() : s);
	}

	/**
	 * Get all known values. The returned set may not be modified.
	 * The method returns an empty set instead of <code>null</code> when
	 * the mappings are empty.
	 * @return the set of values
	 */
	public Set getValues() {
		Set s = valueKeyMap.keySet();
		return Collections.unmodifiableSet(s == null ? new LinkedHashSet() : s);
	}

	/**
	 * Check if the mapping stores the given key.
	 * @param key the key which has to map to at least one value
	 * @return <code>true</code> if the key is present
	 */
	public boolean hasKey(Object key) {
		return keyValueMap.containsKey(key);
	}
	
	/**
	 * Check if the mapping stores the given value.
	 * @param value the value which requires at least one key mapping to it
	 * @return <code>true</code> if the value is present
	 */
	public boolean hasValue(Object value) {
		return valueKeyMap.containsKey(value);
	}
	
	/**
	 * Get all values the given key maps to. The returned set may not be modified.
	 * @param key the key
	 * @return the values or an empty set if the key does not exist
	 */
	public Set getValues(Object key) {
		Set s = (Set)keyValueMap.get(key);
		return Collections.unmodifiableSet(s == null ? new LinkedHashSet() : s);
	}

	/**
	 * Get all keys mapping to the value. The returned set may not be modified.
	 * @param value the value
	 * @return the keys or an empty set if the value does not exist
	 */
	public Set getKeys(Object value) {
		Set s = (Set)valueKeyMap.get(value);
		return Collections.unmodifiableSet(s == null ? new LinkedHashSet() : s);
	}

	/**
	 * Remove all content from this mapping.
	 */
	public void clear() {
		keyValueMap.clear();
		valueKeyMap.clear();
	}

	/**
	 * Append the mapping key->{value} to the existing ones.
	 * @param key the key
	 * @param value the value to be appended to this key
	 * @param addedValues the set storing the values if it was newly inserted
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was added
	 */
	public boolean addToKey(Object key, Object value, Set addedValues) {
		Set valueSet = new LinkedHashSet();
		valueSet.add(value);
		return addToKey(key, valueSet, addedValues);
	}

	/**
	 * Append the mappings key->{value_1, ..., value_n} to the existing ones.
	 * @param key the key
	 * @param valueSet the values to be appended to this key
	 * @param addedValues the set storing all new included values
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was added
	 */
	public boolean addToKey(Object key, Set valueSet, Set addedValues) {
		return addToKey(key, valueSet, keyValueMap, valueKeyMap, addedValues);
	}

	/**
	 * Remove the mapping key->{value} from the existing ones.
	 * @param key the key
	 * @param value the value to be removed from the key
	 * @param removedValues the set storing this value if it was completely removed by this operation
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if the value was completely removed
	 */
	public boolean removeFromKey(Object key, Object value, Set removedValues) {
		Set valueSet = new LinkedHashSet();
		valueSet.add(value);
		return removeFromKey(key, valueSet, removedValues);
	}

	/**
	 * Remove the mappings key->{value_1, ..., value_n} from the existing ones.
	 * @param key the key
	 * @param valueSet the values to be removed from the key
	 * @param removedValues the set storing all values which were completely removed by this operation
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was completely removed
	 */
	public boolean removeFromKey(Object key, Set valueSet, Set removedValues) {
		return removeFromKey(key, valueSet, keyValueMap, valueKeyMap, removedValues);
	}

	/**
	 * Put the mapping key->{value_1,...,value_n}. If present, the previous mapping is removed.
	 * Note, removal of the previous mapping may cause removal of values. Note further, adding
	 * a mapping to an empty value set is the same as removing the key.
	 * @param key the key
	 * @param valueSet the set of values
	 * @param addedValues the set of new included values
	 * (if <code>null</code> this parameter is ignored)
	 * @param removedValues the set of removed values
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was added or removed
	 */
	public boolean putKey(Object key, Set valueSet, Set addedValues, Set removedValues) {
		return put(key, valueSet, keyValueMap, valueKeyMap, addedValues, removedValues);
	}

	/**
	 * Put the mapping value->{key_1,...,key_n}. If present, the previous mapping is removed.
	 * Note, removal of the previous mapping may cause removal of keys. Note further, adding
	 * a mapping to an empty key set is the same as removing the value.
	 * @param value the value
	 * @param keySet the set of keys
	 * @param addedKeys the set of new included keys
	 * (if <code>null</code> this parameter is ignored)
	 * @param removedKeys the set of removed keys
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any key was added or removed
	 */
	public boolean putValue(Object value, Set keySet, Set addedKeys, Set removedKeys) {
		return put(value, keySet, valueKeyMap, keyValueMap, addedKeys, removedKeys);
	}

	/**
	 * Put the mapping key->{value}. If present, the previous mapping is removed.
	 * Note, removal of the previous mapping may cause removal of values.
	 * @param key the key
	 * @param value the value
	 * @param addedValues the set of new included values which is at most one element
	 * (if <code>null</code> this parameter is ignored)
	 * @param removedValues the set of removed values
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was added or removed
	 */
	public boolean putKey(Object key, Object value, Set addedValues, Set removedValues) {
		Set valueSet = new LinkedHashSet();
		valueSet.add(value);
		return putKey(key, valueSet, addedValues, removedValues);
	}

	/**
	 * Put the mapping value->{key}. If present, the previous mapping is removed.
	 * Note, removal of the previous mapping may cause removal of keys.
	 * @param value the value
	 * @param key the key
	 * @param addedKeys the set of new included keys which at most one element
	 * (if <code>null</code> this parameter is ignored)
	 * @param removedKeys the set of removed keys
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any key was added or removed
	 */
	public boolean putValue(Object value, Object key, Set addedKeys, Set removedKeys) {
		Set keySet = new LinkedHashSet();
		keySet.add(key);
		return putKey(value, keySet, addedKeys, removedKeys);
	}

	/**
	 * Remove the mapping from key to values. All values with no remaining key are removed.
	 * @param key the key
	 * @param removedValues the set of removed values
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any value was removed
	 */
	public boolean removeKey(Object key, Set removedValues) {
		return remove(key, keyValueMap, valueKeyMap, removedValues);
	}

	/**
	 * Remove the mapping from value to keys. All keys with no remaining values are removed.
	 * @param value the value
	 * @param removedKeys the set of removed keys
	 * (if <code>null</code> this parameter is ignored)
	 * @return <code>true</code> if any key was removed
	 */
	public boolean removeValue(Object value, Set removedKeys) {
		return remove(value, valueKeyMap, keyValueMap, removedKeys);
	}

	// Append the mapping from key to values and value to keys, respectively.
	// The method returns true if any value was added.
	private boolean addToKey(Object key, Set valueSet, Map keyValueMap, Map valueKeyMap, Set addedValueSet) {
		
		// appending an empty set has no effect
		if(valueSet.isEmpty()) {
			return false;
		}

		// get the current set the key is mapping to
		Set oldValueSet = (Set) keyValueMap.get(key);
		if(oldValueSet == null) {
			oldValueSet = new LinkedHashSet();
			keyValueMap.put(key, oldValueSet);
		}

		// used to remember any value set changes
		boolean added = false;

		// insert new values
		Iterator iterator = valueSet.iterator();
		while (iterator.hasNext()) {
			Object value = iterator.next();
			if(!oldValueSet.contains(value)) {
				oldValueSet.add(value);
				added |= addToMap(valueKeyMap, value, key, addedValueSet);
			}
		}
		
		// return true if any value was added
		return added;

	}

	// Remove values from the value set this key points to.
	// The method returns true if the operation lead to removal
	// of a value from the set of all values.
	private boolean removeFromKey(Object key, Set valueSet, Map keyValueMap, Map valueKeyMap, Set removedValueSet) {
		
		// removing an empty set of values has no effect
		if(valueSet.isEmpty()) {
			return false;
		}

		// get the current set the key is mapping to
		Set oldValueSet = (Set) keyValueMap.get(key);
		
		// no action required when the key does not exist
		if(oldValueSet == null) {
			return false;
		}

		// used to remember any value set changes
		boolean removed = false;

		// remove values
		Iterator iterator = valueSet.iterator();
		while (iterator.hasNext()) {
			Object value = iterator.next();
			if(oldValueSet.remove(value)) {
				removed |= removeFromMap(valueKeyMap, value, key, removedValueSet);
			}
		}

		// remove key when it points to no more values
		if(oldValueSet.isEmpty()) {
			keyValueMap.remove(key);
		}
		
		// return true if any value was added
		return removed;

	}

	// Put new mapping from key to values and value to keys, respectively.
	// The method returns true if any value was added or removed.
	private boolean put(Object key, Set valueSet, Map keyValueMap, Map valueKeyMap, Set addedValueSet, Set removedValueSet) {

		// adding a key mapping to an empty set is the same as removing the key
		if(valueSet.isEmpty()) {
			return remove(key, keyValueMap, valueKeyMap, removedValueSet);
		}
		
		// used to remember any value set changes
		boolean changed = false;
		
		// store new mapping
		Set oldValueSet = (Set) keyValueMap.put(key, valueSet);

		// insert key in value key map
		Iterator iterator = valueSet.iterator();
		while (iterator.hasNext()) {
			Object value = iterator.next();
			changed |= addToMap(valueKeyMap, value, key, addedValueSet);
		}

		// remove old mappings from key to value
		if (oldValueSet != null) {
			iterator = oldValueSet.iterator();
			while (iterator.hasNext()) {
				Object oldValue = iterator.next();
				if (!valueSet.contains(oldValue)) {
					changed |= removeFromMap(valueKeyMap, oldValue, key, removedValueSet);
				}
			}
		}
		
		// return true if there was any change to value set
		return changed;
		
	}

	// Remove key from mapping and add these values to the set of removed ones
	// if there exists no more key pointing to that value. The method returns true
	// if any value was removed.
	private boolean remove(Object key, Map keyValueMap, Map valueKeyMap, Set removedValues) {
		boolean removed = false;
		Set oldValueSet = (Set) keyValueMap.remove(key);
		if(oldValueSet != null) {
			Iterator iterator = oldValueSet.iterator();
			while (iterator.hasNext()) {
				Object oldValue = iterator.next();
				removed |= removeFromMap(valueKeyMap, oldValue, key, removedValues);
			}
		}
		return removed;
	}

	// Add a value to a map where key maps to a set of values. When the set
	// gets created the key is added to the added keys set. The method returns
	// true if the key was added.
	private boolean addToMap(Map map, Object key, Object value, Set addedKeys) {
		boolean added = false;
		Set valueSet = (Set) map.get(key);
		if (valueSet == null) {
			valueSet = new LinkedHashSet();
			map.put(key, valueSet);
			addToSet(addedKeys, key);
			added = true;
		}
		valueSet.add(value);
		return added;
	}

	// Remove a value from a map where key maps to a set of values. When the set
	// gets empty the mapping is removed and the key is added to the removed set.
	// The method returns true if the key was removed.
	private boolean removeFromMap(Map map, Object key, Object value, Set removedKeys) {
		boolean removed = false;
		Set set = (Set) map.get(key);
		set.remove(value);
		if (set.isEmpty()) {
			map.remove(key);
			addToSet(removedKeys, key);
			removed = true;
		}
		return removed;
	}

	// Add a value to a set which can be null.
	private void addToSet(Set set, Object value) {
		if(set != null) {
			set.add(value);
		}
	}
	
	/**
	 * Main method used to test the implementation.
	 * @param args no args required
	 */
	public static void main(String[] args) {
		
		SymmetricMapping symmetricMapping = new SymmetricMapping();
		Set added = new LinkedHashSet();
		Set removed = new LinkedHashSet();

		printAll("initialized", symmetricMapping, null, null);

		symmetricMapping.putKey(new Integer(1), createSet(new Object[] { "a", "c", "f" }), added, removed);
		printAll("putKey 1->{a,c,f}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.putKey(new Integer(2), createSet(new Object[] { "b", "c" }), added, removed);
		printAll("putKey 2->{b,c}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.putKey(new Integer(3), createSet(new Object[] { "a", "d"}), added, removed);
		printAll("putKey 3->{a,d}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeKey(new Integer(3), removed);
		printAll("removeKey 3", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeKey(new Integer(3), removed);
		printAll("removeKey 3", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeValue("c", removed);
		printAll("removeValue c", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeValue("b", removed);
		printAll("removeValue b", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.putValue("b", createSet(new Object[] { new Integer(2), new Integer(3)}), added, removed);
		printAll("putValue b->{2,3}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.putValue("b", createSet(new Object[] { new Integer(1), new Integer(2)}), added, removed);
		printAll("putValue b->{1,2}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.putKey(new Integer(1), createSet(new Object[] { "f" }), added, removed);
		printAll("putKey 1->{f}", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeValue("f", removed);
		printAll("removeValue f", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeValue("f", removed);
		printAll("removeValue f", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

		symmetricMapping.removeValue("b", removed);
		printAll("removeValue b", symmetricMapping, added, removed);
		added.clear();
		removed.clear();

	}

	// create a set from an array of objects
	private static Set createSet(Object[] values) {
		Set result = new LinkedHashSet();
		for(int i=0; i<values.length; i++) {
			result.add(values[i]);
		}
		return result;
	}
	
	// print mapping, added and removed after runnint the command
	private static void printAll(String command, SymmetricMapping mapping, Set added, Set removed) {
		System.out.println(command);
		System.out.println("mapping : " + mapping);
		if(added != null) {
			System.out.println("added   : " + added);
		}
		if(removed != null) {
			System.out.println("removed : " + removed);
		}
		System.out.println();
	}
	
}
