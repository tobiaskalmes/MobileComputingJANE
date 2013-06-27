package de.uni_trier.jane.tools.pathneteditor.tools;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * allows proportional changes of sliders in a group.
 */
public class SliderManager {

	private Vector listener = new Vector();
	private Vector slider_sets = new Vector();
	private Vector sorted_sets = new Vector();
	private JSlider[] slider_cache = null;
	private boolean slider_cache_changed = true;
	private JCheckBox[] checkbox_cache = null;
	private boolean checkbox_cache_changed = true;
	private int min = 0;
	private int max = 100;
	private Comparator set_comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
			SliderSet set1 = (SliderSet)o1;
			SliderSet set2 = (SliderSet)o2;
			if (set1.getExactValue()<set2.getExactValue())
				return 1;
			else if (set1.getExactValue()>set2.getExactValue())
				return -1;
			else
				return 0;
		}
	};
	
	/*
	 *******************************************
	 *  constructors
	 ******************************************* 
	 */
	
	public SliderManager() {
	}
	
	public SliderManager(int min, int max) {
		this();
		this.min = min;
		this.max = max;
	}
	
	/*
	 *******************************************
	 *  
	 ******************************************* 
	 */
	
	public void reset() {
		clearSliderListener();
		slider_sets.clear();
		sorted_sets.clear();
		slider_cache_changed = true;
		checkbox_cache_changed = true;
	}
	
	
	/*
	 *******************************************
	 *  listener
	 ******************************************* 
	 */
	public void addSliderListener(SliderListener listener) {
		if (!this.listener.contains(listener))
			this.listener.add(listener);
	}
	
	public boolean removeSliderListener(SliderListener listener) {
		return this.listener.remove(listener);
	}
	
	public void clearSliderListener() {
		listener.clear();
	}
	
	public SliderListener[] getSliderListener() {
		if (listener.size()==0)
			return null;
		SliderListener[] result = new SliderListener[listener.size()];
		for (int i=0; i<result.length; i++)
			result[i] = (SliderListener) listener.get(i);
		return result;
	}

	private void sliderStateChanged(JSlider source) {
		if (!getSliderSet(source).listener_activated)
			return;
		setListenerActivated(false);
		SliderEvent event = new SliderEvent(source);

		// update other sliders
		updateSliderValues(source);
		setListenerActivated(true);
		
		// call listener 
		for (int i=0; i<listener.size(); i++)
			((SliderListener)listener.get(i)).sliderValuesChanged(event);
	}
	
	private void checkBoxStateChanged(JCheckBox source) {
		updateEnabled();
		// TODO: call listener? -> extend listener interface...
	}
	
	/*
	 *******************************************
	 *  slider
	 ******************************************* 
	 */
	
	/**
	 * add a slider to the manager (the values of all sliders together must equal the max specified).
	 */
//	public void addSlider(int value) {
//		DefaultBoundedRangeModel brm = new DefaultBoundedRangeModel(value, 0, min, max);
//		SliderSet set = new SliderSet(new JSlider(brm));
//		set.last_value = value;
//		set.setExactValue(value);
//		slider_sets.add(set); 	// add to slider sets
//		sorted_sets.add(set);	// add to sorted_sets (sorting will be done when needed.)
//		slider_cache_changed = true;
//	}
	
	public void addSlider(JSlider slider) {
		DefaultBoundedRangeModel brm = new DefaultBoundedRangeModel(slider.getValue(), 0, min, max);
		slider.setModel(brm);
		SliderSet set = new SliderSet(slider);
		set.last_value = slider.getValue();
		set.setExactValue(slider.getValue());
		slider_sets.add(set);
		sorted_sets.add(set);
		slider_cache_changed = true;
	}
	
	public JCheckBox getCheckBox(JSlider slider) {
		return getSliderSet(slider) != null ? getSliderSet(slider).checkbox : null;
	}
	
	public JSlider getSlider(int pos) {
		return ((SliderSet)slider_sets.get(pos)).slider;
	}
	
	public JSlider[] getSliders() {
		if (slider_cache_changed)
			updateSliderCache();
		return slider_cache;
	}
	
	private void updateSliderCache() {
		slider_cache = new JSlider[slider_sets.size()];
		for (int i=0; i<slider_cache.length; i++)
			slider_cache[i] = ((SliderSet)slider_sets.get(i)).slider;
		slider_cache_changed = false;
	}
	
	/**
	 * activates/deactivates listener for all sliders
	 */
	private void setListenerActivated(boolean value) {
		for (int i=0; i<slider_sets.size(); i++) {
			((SliderSet)slider_sets.get(i)).listener_activated = value;
		}
	}
	
	/**
	 * check how many sliders are enabled (not locked). if there is only one left
	 * it cannot be changed because otherwise there would be no slider to compensate
	 * for the amount changed -> disable last slider.
	 *
	 */
	private void updateEnabled() {
		int pos = getSliderToDisable();
		for (int i=0; i<slider_sets.size(); i++) {
			SliderSet set = (SliderSet) slider_sets.get(i);
			set.slider.setEnabled(!set.checkbox.isSelected());
		}
		if (pos>=0)
			getSlider(pos).setEnabled(false);
	}
	
	private int getSliderToDisable() {
		int pos = -1;
		for (int i=0; i<slider_sets.size(); i++) {
			if (!((SliderSet)slider_sets.get(i)).checkbox.isSelected())
				if (pos>-1)
					return -1;		// the second active one found
				else
					pos=i; 	// an active one found
		}
		return pos;
	}
	
	private SliderSet[] getSortedSets() {
		SliderSet[] sets = new SliderSet[sorted_sets.size()];
		for (int i=0; i<sets.length; i++)
			sets[i] = (SliderSet) sorted_sets.get(i);
		return sets;
	}

	private void updateExactValues(JSlider source) {
//		System.out.println("updating exact values");
//		double rounded_off = 0.0; // the amount that is rounded off
		for (int i=0; i<slider_sets.size(); i++) {
			SliderSet set = (SliderSet)slider_sets.get(i);
			int rounded = (int) Math.round(set.getExactValue());
//			rounded_off += (rounded-set.getExactValue());
			set.slider.setValue(rounded);
		}
		for (int i=0; i<sorted_sets.size(); i++) {
//			System.out.print("looking for biggest slider... ");
			SliderSet set = (SliderSet) sorted_sets.get(i);
			if (set.slider!=source && !set.checkbox.isSelected()) {
//				System.out.println("found!");
				set.slider.setValue(max-getSliderValuesSum(set.slider));
				break;
			} 
//			else
//				System.out.println("not this one.");
		}
//		System.out.println("update done. exact values are:");
//		double sum = 0;
//		for (int i=0; i<slider_sets.size(); i++) {
//			System.out.println("  " + ((SliderSet)slider_sets.get(i)).getExactValue());
//			sum+=((SliderSet)slider_sets.get(i)).getExactValue();
//		}
//		System.out.println("total: " + sum);

	}
	
	/**
	 * calculate the sum from all values except the source one
	 * @param exception
	 * @return
	 */
	private int getSliderValuesSum(JSlider exception) {
		int sum = 0;
		for (int i=0; i<slider_sets.size(); i++) {
			SliderSet set = (SliderSet) slider_sets.get(i);
			if (set.slider!=exception)
				sum += set.slider.getValue();
		}
		return sum;
	}
	
	/**
	 * update all slider values according to the one changed.
	 *
	 */
	private void updateSliderValues(JSlider source) {
		if (getSliderSet(source).last_value==source.getValue())
			return;
		int sum = getAllLockedSlidersValue();
//		System.out.println("#################");
//		System.out.println("last: " + getSliderSet(source).last_value);
//		System.out.println("value: " + source.getValue() + " sum of locked sliders: " + sum);
		if (source.getValue()+sum>max) {// value is too big
			source.setValue(max-sum);
			source.updateUI();
			/**
			 * FIXME: this is UGLY, we know, but it's a minor bug in jslider/jspinner
			 * the slider is not repainted correctly... invalidate() + validate() + repaint() does NOT work...
			 */
		}
		// sort sets
		updateSortedSets();
		
		// compute amount
		SliderSet set = getSliderSet(source);
		double amount = set.getExactValue() - source.getValue();
		
//		System.out.println("##### calling update sliders #####");
		// call recursive method
		updateSliders(getSortedSets(), getActiveSortedSliders(), 0, source, amount);
//		System.out.println("##### calling update sliders ended #####");
		
		// save last value
		set.last_value = source.getValue();
		set.setExactValue(source.getValue());
		
		// set real values from the exact ones
		updateExactValues(source);
	}
	
	/**
	 * update the slider values proportionally
	 * @param sorted_set	the SliderSets
	 * @param active_set	the positions in the sorted set that are activated (not locked)
	 * @param from_pos		apply changes to all elements from that position
	 * @param source		this is the JSlider that caused the update (it won't be changed)
	 * @param amount		the amount to distribute among the sliders
	 */
	private void updateSliders(SliderSet[] sorted_set, int[] active_set, int from_pos, JSlider source, double amount) {
//		System.out.println("updating sliders " + from_pos + "(" + active_set[from_pos] + ") amount=" + amount + " has exact value of " + sorted_set[active_set[from_pos]].getExactValue());
		SliderSet set = sorted_set[active_set[from_pos]];
		double famount = 0;
		if (set.slider!=source) {
			double factor = getProportionFactor(sorted_set, active_set, from_pos, source);
			famount = amount * factor;
//			System.out.println("factor: " + factor + " -> amount: " + famount);
//			if (amount>0 && set.getExactValue() + famount > max)
//				;
//			else if (amount<0 && set.getExactValue() - famount < 0)
//				;
			set.setExactValue(set.getExactValue() + famount);
		} else {
//			System.out.println("is source slider -> don't change value");
		}
//		System.out.println("update done. exact values are:");
//		double sum = 0;
//		for (int i=0;i <active_set.length; i++) {
//			System.out.println("  " + sorted_set[active_set[i]].getExactValue());
//			sum+=sorted_set[active_set[i]].getExactValue();
//		}
//		System.out.println("total: " + sum);
		if (from_pos<active_set.length-1)
			updateSliders(sorted_set, active_set, from_pos+1, source, amount-famount);
	}
	
	/**
	 * returns the factor to apply to the amount for this slider (at pos sorted_set[from_pos])
	 * @param sorted_set
	 * @param active_set
	 * @param from_pos
	 * @param source
	 * @return
	 */
	private double getProportionFactor(SliderSet[] sorted_set, int[] active_set, int from_pos, JSlider source) {
		double total = 0;
		for (int i=from_pos; i<active_set.length; i++) {
			if (sorted_set[active_set[i]].slider==source)
				continue;
			total += sorted_set[active_set[i]].getExactValue();
		}
//		System.out.println("total: " + total + ", exact(from_pos): " + sorted_set[active_set[from_pos]].getExactValue());
		return sorted_set[active_set[from_pos]].getExactValue() / (total==0?1:total);
	}
	
	private SliderSet getSliderSet(JSlider slider) {
		for (int i=0; i<slider_sets.size(); i++) {
			if (((SliderSet)slider_sets.get(i)).slider==slider)
				return (SliderSet)slider_sets.get(i);
		}
		return null;
	}
	
	private int getAllLockedSlidersValue() {
		SliderSet[] set = getLockedSliderSets();
		int sum = 0;
		for (int i=0; i<set.length; i++) {
			sum+=set[i].slider.getValue();
		}
		return sum;
	}
	
	private int countActiveSliders() {
		int counter = 0;
		for (int i=0; i<slider_sets.size(); i++) {
			if (!((SliderSet)slider_sets.get(i)).checkbox.isSelected())
				counter++; // an active one found
		}
		return counter;
	}
	
	/**
	 * returns the position of the active (not locked) sliders from the original slider_sets.
	 * @return
	 */
	private int[] getActiveSliders() {
		int[] result = new int[countActiveSliders()];
		int current = 0;
		for (int i=0; i<slider_sets.size(); i++) {
			if (!((SliderSet)slider_sets.get(i)).checkbox.isSelected())
				result[current++] = i;
		}
		return result;
	}

	/**
	 * returns the position of the active (not locked) sliders from the copied sorted_slider_sets.
	 * @return
	 */
	private int[] getActiveSortedSliders() {
		int[] result = new int[countActiveSliders()];
		int current = 0;
		for (int i=0; i<sorted_sets.size(); i++) {
			if (!((SliderSet)sorted_sets.get(i)).checkbox.isSelected())
				result[current++] = i;
		}
		return result;
	}
	
	private SliderSet[] getLockedSliderSets() {
		SliderSet[] sets = new SliderSet[slider_sets.size()-countActiveSliders()];
		int current = 0;
		for (int i=0; i<slider_sets.size(); i++) {
			if (((SliderSet)slider_sets.get(i)).checkbox.isSelected())
				sets[current++] = (SliderSet)slider_sets.get(i);
		}
		return sets;
	}
	
	public JCheckBox getCheckBox(int pos) {
		return ((SliderSet)slider_sets.get(pos)).checkbox;
	}
	
	public JCheckBox[] getCheckBoxes() {
		if (checkbox_cache_changed)
			updateCheckBoxCache();
		return checkbox_cache;
	}
	
	private void updateCheckBoxCache() {
		checkbox_cache = new JCheckBox[slider_sets.size()];
		for (int i=0; i<checkbox_cache.length; i++)
			checkbox_cache[i] = ((SliderSet)slider_sets.get(i)).checkbox;
		checkbox_cache_changed = false;
	}
	
	private void updateSortedSets() {
		Collections.sort(sorted_sets, set_comparator);
	}
	
	/*
	 ********************************************************
	 * classes
	 ********************************************************
	 */
	
	private class SliderSet {
		public JSlider slider = null;
		public JCheckBox checkbox = null;
		private double exactValue = 0.0;
		public int last_value = 0;
		public boolean listener_activated = true;
		
		public SliderSet(JSlider slider) {
			if (slider==null)
				throw new IllegalArgumentException("The slider cannot be null.");
			
			// slider
			this.slider = slider;
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					SliderManager.this.sliderStateChanged(SliderSet.this.slider);
				}
			});
//			slider.addMouseListener(new MouseAdapter() {
//				public void mouseReleased(MouseEvent e) {
//				}
//			});
			
			// checkbox
			checkbox = new JCheckBox();
			checkbox.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					SliderManager.this.checkBoxStateChanged(checkbox);
				}
			});
		}
		
		public void setExactValue(double d) {
			this.exactValue = Math.max(d, 0.001);
		}
		
		public double getExactValue() {
			return exactValue;
		}
	}
	
	public static interface SliderListener {
		public abstract void sliderValuesChanged(SliderEvent evt);
	}
	
	public static class SliderEvent {
		private JSlider source = null;
		
		public SliderEvent(JSlider source) {
			if (source==null)
				throw new IllegalArgumentException("The source for the event cannot be null.");
			this.source = source;
		}
	}
	
	
	/**
	 * TEST METHOD
	 */
	
//	public static void main(String[] args) {
//		Tester tester = new Tester();
//		tester.setVisible(true);
//		
//	}
	
//	private static class Tester extends JFrame {
//		private final int MAX = 1000;
//		private SliderManager sm = new SliderManager(0, MAX);
//		private JLabel[] values = null;
//		public Tester() {
//			sm.addSliderListener(new Listener());
//			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			setSize(400,400);
//			getContentPane().setLayout(new GridLayout(0,3));
//			int count = 4;
//			values = new JLabel[count];
//			for (int i=0; i<count; i++) {
//				sm.addSlider(new JSlider(0, 100, MAX/count));
//				getContentPane().add(sm.getSlider(i));
//				getContentPane().add(sm.getCheckBox(i));
//				getContentPane().add(values[i] = new JLabel("0"));
//			}
//		}
//		
//		private class Listener implements SliderListener {
//			public void sliderValuesChanged(SliderEvent evt) {
//				for (int i=0; i<values.length; i++) {
//					values[i].setText(sm.getSlider(i).getValue()+"");
//				}
//			}
//		}
//	}
	
}
