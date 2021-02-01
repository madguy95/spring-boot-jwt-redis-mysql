package com.springjwt;

public class Main {

	static class Base {
		public static void printValue() {
		    System.out.println("  Called static Base method.");
		  }
		  public void nonStatPrintValue() {
		    System.out.println("  Called non-static Base method.");
		  }
		  public void nonLocalIndirectStatMethod() {
		    System.out.println("  Non-static calls overridden(?) static:");
		    System.out.print("  ");
		    this.printValue();
		  }
		}
	static class Child extends Base {
		public static void printValue() {
		    System.out.println("  Called static Child method.");
		  }
		  public void nonStatPrintValue() {
		    System.out.println("  Called non-static Child method.");
		  }
		  public void localIndirectStatMethod() {
		    System.out.println("  Non-static calls own static:");
		    System.out.print("  ");
		    printValue();
		  }
	}
	public static void main(String[] args) {
		System.out.println("Object: static type Base; runtime type Child:");
	    Base base = new Child();
	    base.printValue();
	    base.nonStatPrintValue();
	    System.out.println("Object: static type Child; runtime type Child:");
	    Child child = new Child();
	    child.printValue();
	    child.nonStatPrintValue();
	    System.out.println("Class: Child static call:");
	    Child.printValue();
	    System.out.println("Class: Base static call:");
	    Base.printValue();
	    System.out.println("Object: static/runtime type Child -- call static from non-static method of Child:");
	    child.localIndirectStatMethod();
	    System.out.println("Object: static/runtime type Child -- call static from non-static method of Base:");
	    child.nonLocalIndirectStatMethod();
	    Double a = Double.parseDouble("01");
	    Double b = Double.parseDouble("02.0");
	    System.out.println(a.compareTo(b));
	}
}
