package de.uni_trier.jane.service.routing.transport.routecache;

import java.util.*;


import de.uni_trier.jane.basetypes.*;
import de.uni_trier.jane.service.*;
import de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem;
import de.uni_trier.jane.service.parameter.todo.Parameters;
import de.uni_trier.jane.service.unit.ServiceUnit;
import de.uni_trier.jane.visualization.shapes.Shape;

/**
 * A simple route cache that manages <code>Route</code>s from the current device or object to arbitrary 
 * destinations. The caching strategy used is LRU (Least Recently Used). The cache is only cleaned up, 
 * if the number of current route entries surpasses a configurable amount. No time-dependant clean-up 
 * functionality has been implemented, since the cache is supposed be used in a static network environment 
 * as well. It might make sense to add a generic caching strategy feature, or allow subclasses to change 
 * the LRU strategy.
 * 
 * For one destination multiple routes can be stored; different methods provide easy access to the shortest 
 * <code>Route</code>, as well as to all available ones. Cache entries are <code>TreeMap</code>s, providing 
 * automatical sort functionality within the existings <code>Route</code>s to one destination. The provided
 * natural sort order is ascending according to the <code>Route</code> length.
 * 
 * Note: The cache implementation is not synchronized. Write access should be limited to externally 
 * synchronized threads.
 * 
 * @author HoehfeldA
 */
public class SimpleRouteCache extends HashMap implements RouteCache, RouteCache_sync, RuntimeService
{
    /**
     * The number of entries in the cache
     */
    private int maxEntries = 100;
    
    /**
     * The currently used operating system
     */
    private RuntimeOperatingSystem runtimeOperatingSystem;
    
    /**
     * The unique <code>ServiceID</code> of the <code>SimpleRouteCache</code> service
     */
    public static ServiceID SERVICE_ID = new EndpointClassID(SimpleRouteCache.class.getName());
    
    /**
     * Creates an instance of the <code>SimpleRouteCache</code> service
     * If an instance of the cache already exists within the <code>ServiceUnit</code>
     * the already existing one will be used and its ID returned.
     * @param serviceUnit the <code>ServiceUnit</code>
     * @return the <code>ServiceID</code> of the instance
     */
    public static ServiceID createInstance(ServiceUnit serviceUnit)
    {
        ServiceID routeCacheServiceID;
        
        if (serviceUnit.hasService(SimpleRouteCache.class))
            routeCacheServiceID = serviceUnit.getService(SimpleRouteCache.class);
        else
            routeCacheServiceID = serviceUnit.addService(new SimpleRouteCache());
        return routeCacheServiceID;
    }
    
    
    
    /**
     * Creates an instance of a <code>SimpleRouteCache</code>
     * This method is only used directly in JUnit tests for easy acccess without a running simulation environment
     */
    public SimpleRouteCache() {}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.RuntimeService#start(de.uni_trier.jane.service.operatingSystem.RuntimeOperatingSystem)
     */
    public void start(RuntimeOperatingSystem runtimeOperatingSystem)
    {
        setRuntimeOperatingSystem(runtimeOperatingSystem);
        runtimeOperatingSystem.registerSignalListener(RouteCache.class);
        runtimeOperatingSystem.registerAccessListener(RouteCache_sync.class);
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getServiceID()
     */
    public ServiceID getServiceID()
    {
        return SERVICE_ID;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#finish()
     */
    public void finish()
    {
        clear();
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getShape()
     */
    public Shape getShape()
    {
        // what kind of visualization a cache could have?
        
        return null;
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.Service#getParameters(de.uni_trier.jane.service.parameter.todo.Parameters)
     */
    public void getParameters(Parameters parameters)
    {
        // no return result necessary, since there are no parameters for this service
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache#addRoute(de.uni_trier.jane.basetypes.ID, de.uni_trier.jane.service.routing.transport.routecache.Route)
     */
    public void addRoute(ID target, Route route)
    {
        List routeList = route.getRoute();
        
        int size = routeList.size();
        
        for (int i = 0; i < size - 1; i++)
            addSubRoute(new Route(routeList.subList(0, i + 2)));
    }
    
    /**
     * Adds the specified sub route to the <code>RouteCache</code>
     * @param routeList the <code>List</code> containing the subroute entries
     */
    private void addSubRoute(Route subRoute)
    {
        List routeList = subRoute.getRoute();
        
        ID target = (ID) routeList.get(routeList.size() - 1);
        
        TreeSet cacheElement = (TreeSet) get(target);
        
        // not a 100% performant implementation
        
        if (cacheElement == null)
        {
            cacheElement = new TreeSet(new RouteComparator());
            put(target, cacheElement);
        }
        
        if (!cacheElement.contains(subRoute))
            cacheElement.add(subRoute);
    }
    
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#getRoute(de.uni_trier.jane.basetypes.ID)
     */
    public Route getRoute(ID target)
    {
    	TreeSet cacheElement = null;
        try
        {
        	cacheElement = (TreeSet) get(target);
        	if (cacheElement != null)
                return (Route)cacheElement.first();
        }
        catch(NoSuchElementException e)
        {
        	System.out.println("ID: "+target);
        	System.out.println("cacheElement: "+cacheElement);
        	System.out.println("cacheElement.size(): "+cacheElement.size());
        	System.exit(0);
        }
        
        
        return null;
    }
    
    
    public Collection removeBrokenLink(Address neighbor) {
    	ArrayList cacheRemovals = new ArrayList();
		ArrayList removals = new ArrayList();
		
		Iterator iterator = entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry entry = (Map.Entry) iterator.next();
			
			TreeSet cacheElement = (TreeSet) entry.getValue();
			
			Iterator routeIterator = cacheElement.iterator();
			while(routeIterator.hasNext())
			{
				Route route = (Route) routeIterator.next();
				if (route.startsWith(neighbor))
					removals.add(route);
			}
			
			cacheElement.removeAll(removals);
			
			if (cacheElement.isEmpty())
				cacheRemovals.add(entry.getKey());
			
			removals.clear();
		}
		
		iterator = cacheRemovals.iterator();
        
        // Remove empty cache entries
        
        while (iterator.hasNext())
            remove((ID) iterator.next());
        return cacheRemovals;
		
	}
    	
    
    /* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#removeBrokenLink(java.util.List)
	 */
	public Collection removeBrokenLink(Route brokenLink)
	{
		ArrayList cacheRemovals = new ArrayList();
		ArrayList removals = new ArrayList();
		
		Iterator iterator = entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry entry = (Map.Entry) iterator.next();
			
			TreeSet cacheElement = (TreeSet) entry.getValue();
			
			Iterator routeIterator = cacheElement.iterator();
			while(routeIterator.hasNext())
			{
				Route route = (Route) routeIterator.next();
				if (route.containsSubRoute(brokenLink))
					removals.add(route);
			}
			
			cacheElement.removeAll(removals);
			
			if (cacheElement.isEmpty())
				cacheRemovals.add(entry.getKey());
			
			removals.clear();
		}
		
		iterator = cacheRemovals.iterator();
        
        // Remove empty cache entries
        
        while (iterator.hasNext())
            remove((ID) iterator.next());
        return cacheRemovals;
		
	}

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#getAllRoutes(de.uni_trier.jane.basetypes.ID)
     */
    public Route[] getAllRoutes(ID target)
    {
        TreeSet cacheElement = (TreeSet) get(target);
        
        if (cacheElement != null)
        {
            Route[] result = new Route[cacheElement.size()];
            cacheElement.toArray(result);
            return result;
        }

        return new Route[0];
    }

    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#hasRoute(de.uni_trier.jane.basetypes.ID)
     */
    public boolean hasRoute(ID target)
    {
        TreeSet cacheElement = (TreeSet) get(target);
        
        if (cacheElement != null)
            return cacheElement.size() > 0;

        return false;
    }
    
    /**
     * Returns the number of <code>Route</code>s to the specified device of object
     * @param target the specified device of object
     * @return the number of <code>Route</code>s to the specified device of object
     */
    public int getRouteCount(ID target)
    {
        TreeSet cacheElement = (TreeSet) get(target);
        
        if (cacheElement != null)
            return cacheElement.size();

        return 0;
    }
    
    /* (non-Javadoc)
     * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#removeRoute(de.uni_trier.jane.service.routing.transport.routecache.Route)
     */
    public void removeRoute(Route subRoute)
    {
        List removedEntries = new ArrayList();
        
        Iterator iteratorEntries = entrySet().iterator();
        while (iteratorEntries.hasNext())
        {
            Map.Entry entry = (Map.Entry) iteratorEntries.next();
            
            TreeSet cacheElement = (TreeSet) entry.getValue();
            
            Iterator iteratorRoutes = cacheElement.iterator();
            
            List removedRoutes = new ArrayList();
            
            while (iteratorRoutes.hasNext())
            {
                Route route = (Route) iteratorRoutes.next();
                
                if (route.containsSubRoute(subRoute))
                    removedRoutes.add(route);
            }
            
            cacheElement.removeAll(removedRoutes);
            
            if (cacheElement.isEmpty())
                removedEntries.add(entry.getKey());
        }
        
        iteratorEntries = removedEntries.iterator();
        
        // Remove empty cache entries
        
        while (iteratorEntries.hasNext())
            remove((ID) iteratorEntries.next());
    }
    
    /* (non-Javadoc)
     * @see java.util.HashMap#removeEldestEntry(java.util.Map.Entry eldest)
     */
    protected boolean removeEldestEntry(Map.Entry eldest)
    {
        return size() > maxEntries;
    }

    /**
     * Returns the maximum number of entries in this cache
     * @return the maximum number of entries in this cache
     */
    public int getMaxEntries()
    {
        return maxEntries;
    }

    /**
     * Sets the maximum number of entries in this cache
     * @param maxEntries the maximum number of entries in this cache
     */
    public void setMaxEntries(int maxEntries)
    {
        this.maxEntries = maxEntries;
        
        // TODO: Refresh the cache if the number of entries has decreased
    }

    /**
     * Returns the <code>RuntimeOperatingSystem</code> of the associated <code>ServiceUnit</code>
     * @return The <code>RuntimeOperatingSystem</code> of the associated <code>ServiceUnit</code>
     */
    public RuntimeOperatingSystem getRuntimeOperatingSystem()
    {
        return runtimeOperatingSystem;
    }
    
    /**
     * Sets the <code>RuntimeOperatingSystem</code> of the associated <code>ServiceUnit</code>
     * @param runtimeOperatingSystem the <code>RuntimeOperatingSystem</code> of the associated <code>ServiceUnit</code>
     */
    public void setRuntimeOperatingSystem(RuntimeOperatingSystem runtimeOperatingSystem)
    {
        this.runtimeOperatingSystem = runtimeOperatingSystem;
    }
    
    /**
     * A class that compares routes by their number of hops.
     * @author HoehfeldA
     */
    private class RouteComparator implements Comparator
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object arg0, java.lang.object arg1)
         */
        public int compare(Object arg0, Object arg1)
        {
            if (arg0 instanceof Route && arg1 instanceof Route)
            {
                Route route1 = (Route) arg0;
                Route route2 = (Route) arg1;
                
                if (route1.getRoute().size() < route2.getRoute().size())
                    return -1;
                else if (route1.getRoute().size() > route2.getRoute().size())
                    return 1;
                return 0;
            }
            throw new ClassCastException();
        }
    }

	/* (non-Javadoc)
	 * @see de.uni_trier.jane.service.routing.transport.routecache.RouteCache_sync#getAllRoutes()
	 */
	public Route[] getAllRoutes()
	{
		ArrayList result = new ArrayList();
		
		Iterator iterator = entrySet().iterator();
		
		while (iterator.hasNext())
		{
			TreeSet cacheElement = (TreeSet) iterator;
			result.addAll(Arrays.asList(cacheElement.toArray()));
		}
		Route[] routes = new Route[result.size()];
		return (Route[])result.toArray(routes);
	}
}
