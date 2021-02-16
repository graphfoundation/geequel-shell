Name: geequel-shell
Version: ${VERSION}
Release: ${RELEASE}%{?dist}
Summary: Command line shell for ONgDB

License: GPLv3
URL: https://github.com/graphfoundation/geequel-shell
Source0: https://github.com/graphfoundation/geequel-shell/archive/%{version}.tar.gz

#Conflicts:
Requires: which, jre == 1.8.0
BuildArch: noarch
Prefix: /usr

%description
A command line shell where you can execute Geequel against an instance
of ONgDB.

%prep
# This macro will unpack the tarball into the appropriate build directory
# Expects tarball to unpack into a directory called {name}-{version}
#%setup -q
%build
#make clean build

%install
rm -rf ${RPM_BUILD_ROOT}
# Calls make with correct DESTDIR
%make_install prefix=/usr

%clean
rm -rf ${RPM_BUILD_ROOT}

%files
%defattr(-,root,root)
%{_bindir}/geequel-shell
%{_datadir}/geequel-shell
%doc %{_mandir}/man1/geequel-shell.1.gz
