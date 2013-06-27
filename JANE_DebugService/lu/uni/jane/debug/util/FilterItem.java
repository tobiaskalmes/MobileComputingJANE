/**
 * 
 */
package lu.uni.jane.debug.util;

/**
 * @author adrian.andronache
 *
 */
public class FilterItem 
{
	public String NAME;
	public boolean FLAG;
	
	public FilterItem( String name )
	{
		NAME = name;
		FLAG = true;
	}
	
	public FilterItem( String name, boolean flag )
	{
		NAME = name;
		FLAG = flag;
	}
}
